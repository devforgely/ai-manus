package com.devforgely.aimanusbackend.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// if annotation uncommented then the code will run on startup
@Component
public class SpringAiAiInvoke implements CommandLineRunner {
    @Resource
    @Qualifier("openAiChatModel")
    private ChatModel chatModel;

    @Override
    public void run(String... args) {
        AssistantMessage assistantMessage = chatModel.call(new Prompt("Hello World"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());
    }
}
