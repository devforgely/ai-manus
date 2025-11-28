package com.devforgely.aimanusbackend.agents;

import com.devforgely.aimanusbackend.advisors.LoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Agent with independent planning ability, can be consumed directly
 */
@Component
public class AiManus extends ToolCallAgent {
    public AiManus(ToolCallback[] allTools, @Qualifier("googleGenAiChatModel") ChatModel aiChatModel) {
        super(allTools);
        this.setName("aiManus");

        String SYSTEM_PROMPT = """
                You are AiManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `doTerminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);

        // initialise ai client
        ChatClient chatClient = ChatClient.builder(aiChatModel)
                .defaultAdvisors(new LoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
