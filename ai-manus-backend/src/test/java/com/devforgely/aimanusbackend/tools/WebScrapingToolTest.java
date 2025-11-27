package com.devforgely.aimanusbackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WebScrapingToolTest {
    @Test
    void scrapeWebPage() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String url = "https://quotes.toscrape.com/";
        String result = webScrapingTool.scrapeWebPage(url);
        Assertions.assertNotNull(result);
    }
}
