package com.devforgely.aimanusbackend.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PDFGenerationToolTest {
    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "test.pdf";
        String content = "test generation of pdf";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
