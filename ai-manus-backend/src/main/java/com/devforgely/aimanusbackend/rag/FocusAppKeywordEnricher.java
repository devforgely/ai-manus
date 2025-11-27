package com.devforgely.aimanusbackend.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FocusAppKeywordEnricher {
    @Resource
    @Qualifier("googleGenAiChatModel")
    private ChatModel aiChatModel;

    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(aiChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }
}
