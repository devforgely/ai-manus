package com.devforgely.aimanusbackend.demo.invoke;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class LangChainAiInvoke {
    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .apiKey(TestApiKey.API_KEY)
                .modelName("gpt-4o-mini")
                .build();

        String answer = model.chat("Hello World");
        System.out.println(answer);
    }
}
