package com.devforgely.aimanusbackend.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * Custom advisor for RAG
 */
public class FocusAppRagCustomAdvisorFactory {
    public static Advisor createFocusAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // filtering status
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        // create document search
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)
                .similarityThreshold(0.5)
                .topK(3)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(FocusAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}
