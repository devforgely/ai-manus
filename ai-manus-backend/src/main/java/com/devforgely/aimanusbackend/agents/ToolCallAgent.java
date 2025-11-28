package com.devforgely.aimanusbackend.agents;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.devforgely.aimanusbackend.agents.model.AgentResult;
import com.devforgely.aimanusbackend.agents.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic tool calling agent, concrete implementation of think and act
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {
    private final ToolCallback[] availableTools;

    private ChatResponse toolCallChatResponse;

    private final ToolCallingManager toolCallingManager;

    // Disable native Spring AI tool call option, self maintain options and context
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = GoogleGenAiChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }

    /**
     * Process current state and decide next step
     *
     * @return boolean indicating require action
     */
    @Override
    public AgentResult think()
    {
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }

        // use LLM, get result of tool call
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, this.chatOptions);

        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(this.availableTools)
                    .call()
                    .chatResponse();
            // record response, use for deciding action
            this.toolCallChatResponse = chatResponse;
            // analyse tool call result, acquire needed tool
            assert chatResponse != null;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            String result = assistantMessage.getText();
            log.info("{} thinking: {}", getName(), result);
            if (!toolCallList.isEmpty())
            {
                log.info("{} selected {} tool to use", getName(), toolCallList.size());
            }
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("Tool name: %s，Arguments: %s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            // If not required to use tool then return false
            if (toolCallList.isEmpty()) {
                // Only required to manually add assistant message when no tool is used
                getMessageList().add(assistantMessage);
                return new AgentResult(false, result);
            } else {
                // When tool is used, it will automatically record assistant message
                return new AgentResult(true, result);
            }
        } catch (Exception e) {
            log.error("{} thought process encountered a problem: {}", getName(), e.getMessage());
            getMessageList().add(new AssistantMessage("An error occurred while processing: " + e.getMessage()));
            return new AgentResult(false, "Encountered a problem.");
        }
    }

    /**
     * Use tool to act
     *
     * @return execution result
     */
    @Override
    public AgentResult act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return new AgentResult(false, "No required tool calls");
        }
        // call tool
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // record conversation history (assistant message, tool call result)
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "tool " + response.name() + " returned: " + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);

        // determine whether to terminate tool use
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));

        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
            return new AgentResult(false, results);
        }
        return new AgentResult(true, results);
    }
}
