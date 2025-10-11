package com.devforgely.aimanusbackend.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

public class HttpAiInvoke {
    public static void main(String[] args) {
        String apiKey = TestApiKey.API_KEY;

        // request url
        String url = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

        // request json object
        JSONArray messagesJson = new JSONArray();

        // add system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");

        // add user message
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "Who are you?");

        // message array
        messagesJson.add(systemMessage);
        messagesJson.add(userMessage);

        // create complete request
        JSONObject requestJson = new JSONObject();
        requestJson.set("model", "gemini-2.5-flash");
        requestJson.set("messages", messagesJson);

        // send request
        String result = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestJson.toString())
                .execute()
                .body();

        System.out.println(result);
    }
}
