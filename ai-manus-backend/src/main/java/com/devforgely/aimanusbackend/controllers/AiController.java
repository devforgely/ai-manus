package com.devforgely.aimanusbackend.controllers;

import com.devforgely.aimanusbackend.agents.AiManus;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel openAiChatModel;

    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        AiManus aiManus = new AiManus(allTools, openAiChatModel);
        return aiManus.runStream(message);
    }
}
