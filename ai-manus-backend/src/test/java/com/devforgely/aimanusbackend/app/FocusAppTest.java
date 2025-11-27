package com.devforgely.aimanusbackend.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class FocusAppTest {
    @Resource
    private FocusApp focusApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // Round 1
        String message = "Hi, my name is jeff";
        String answer = focusApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // Round 2
        message = "I want to be more productive";
        answer = focusApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // Round 3
        message = "What is my name again?";
        answer = focusApp.doChat(message, chatId);
        Assertions.assertTrue(answer.toLowerCase().contains("jeff"),
                "Expected 'jeff' to be present in the answer, but got: " + answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "Hi, my name is jeff, I want to do productive, can you teach me?";
        FocusApp.FocusReport focusReport = focusApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(focusReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "I can't keep a long term habit, what should I do？";
        String answer = focusApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // test web search answer
        testMessage("I want to be productive in library, can you find me some good places?");

        // test beb scraping
        testMessage("I am learning Angular, can you look into 'angular.dev/installation', for how to install it.");

        // test resource download
        testMessage("Can you download a image of someone working hard on coding.");

        // test command line execution
        testMessage("Execute command line to echo hello world.");

        // test file operation
        testMessage("Save my productive plan as file");

        // test pdf generation
        testMessage("generate a 'Productive Plan' PDF, I will be using it to guide my schedule.");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = focusApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
