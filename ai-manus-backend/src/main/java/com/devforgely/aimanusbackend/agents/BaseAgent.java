package com.devforgely.aimanusbackend.agents;

import cn.hutool.core.util.StrUtil;
import com.devforgely.aimanusbackend.agents.model.AgentResult;
import com.devforgely.aimanusbackend.agents.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public abstract class BaseAgent {
    private String name;

    private String systemPrompt;
    private String nextStepPrompt;

    private AgentState state = AgentState.IDLE;

    private int currentStep = 0;
    private int maxSteps = 10;

    // LLM Model
    private ChatClient chatClient;

    private List<Message> messageList = new ArrayList<>();

    /**
     * Agent Acting
     *
     * @return execution result
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }

        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }

        this.state = AgentState.RUNNING;
        // record context
        messageList.add(new UserMessage(userPrompt));

        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxSteps);
                // execute single step
                AgentResult stepResult = step();
                String result;

                if (!stepResult.act()) {
                    this.state = AgentState.FINISHED;
                    result = stepResult.content();
                }
                else
                {
                    result = "Step " + stepNumber + ": " + stepResult.content();
                }
                results.add(result);
            }
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("error executing agent", e);
            return "Execution Error: " + e.getMessage();
        } finally {
            this.cleanup();
        }
    }

    /**
     * Agent Acting (Stream)
     *
     * @return execution result
     */
    public SseEmitter runStream(String userPrompt) {
        // 5 minutes timeout sseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L);

        // use asynchronous thread to prevent blocking
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    sseEmitter.send("Error, Cannot run agent from stat: " + this.state);
                    sseEmitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    sseEmitter.send("Error, Cannot run agent with empty user prompt");
                    sseEmitter.complete();
                    return;
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }

            this.state = AgentState.RUNNING;
            // record context
            messageList.add(new UserMessage(userPrompt));
            try {
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step {}/{}", stepNumber, maxSteps);
                    // execute single step
                    AgentResult stepResult = step();
                    String result;

                    if (!stepResult.act()) {
                        this.state = AgentState.FINISHED;
                        result = "[Done]" + stepResult.content();
                    }
                    else
                    {
                        result = "Step " + stepNumber + ": " + stepResult.content();
                    }
                    // Emit every result
                    sseEmitter.send(result);
                }
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    sseEmitter.send("[Terminate]" + "Reached max steps (" + maxSteps + ")");
                }
                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("error executing agent", e);
                try {
                    sseEmitter.send("Execution error: " + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                this.cleanup();
            }
        });

        // set timeout fallback
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });

        // set complete call
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });
        return sseEmitter;
    }

    /**
     * Define single agent step
     */
    public abstract AgentResult step();

    /**
     * Resource cleanup
     */
    protected void cleanup() {
        // child class can override for resource cleanup
    }
}
