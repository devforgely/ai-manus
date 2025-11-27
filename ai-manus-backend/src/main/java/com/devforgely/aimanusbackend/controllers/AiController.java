package com.devforgely.aimanusbackend.controllers;

import com.devforgely.aimanusbackend.agents.AiManus;
import com.devforgely.aimanusbackend.app.FocusApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private ToolCallback[] allTools;

    @Resource
    @Qualifier("googleGenAiChatModel")
    private ChatModel aiChatModel;

    @Resource
    private FocusApp focusApp;

    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        AiManus aiManus = new AiManus(allTools, aiChatModel);
        return aiManus.runStream(message);
    }

    /**
     * Use Focus App
     */
    @GetMapping("/focus_app/chat/sync")
    public String doChatWithFocusAppSync(String message, String chatId) {
        return focusApp.doChat(message, chatId);
    }

    @GetMapping(value = "/focus_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithFocusAppSSE(String message, String chatId) {
        return focusApp.doChatByStream(message, chatId);
    }

    @GetMapping(value = "/focus_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithFocusAppServerSentEvent(String message, String chatId) {
        return focusApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @GetMapping(value = "/focus_app/chat/sse_emitter")
    public SseEmitter doChatWithFocusAppServerSseEmitter(String message, String chatId) {
        // create longer timeout SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 minutes
        // get Flux response stream and sent to SseEmitter
        focusApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }
}
