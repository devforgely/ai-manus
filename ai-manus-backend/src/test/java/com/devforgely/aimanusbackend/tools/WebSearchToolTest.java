package com.devforgely.aimanusbackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebSearchToolTest {
    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Test
    void searchWeb() {
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        String query = "codefather.cn";
        String result = webSearchTool.searchWeb(query);
        Assertions.assertNotNull(result);
    }
}
