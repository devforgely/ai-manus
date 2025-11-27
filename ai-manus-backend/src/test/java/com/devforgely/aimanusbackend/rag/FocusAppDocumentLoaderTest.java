package com.devforgely.aimanusbackend.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FocusAppDocumentLoaderTest {
    @Resource
    private FocusAppDocumentLoader focusAppDocumentLoader;

    @Test
    void loadMarkdowns() {
        List<Document> documentList = focusAppDocumentLoader.loadMarkdowns();
        for  (Document document : documentList) {
            System.out.println(document);
        }
        Assertions.assertFalse(documentList.isEmpty());
    }
}
