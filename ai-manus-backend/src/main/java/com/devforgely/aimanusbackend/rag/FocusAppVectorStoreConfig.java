package com.devforgely.aimanusbackend.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class FocusAppVectorStoreConfig {
    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, @Qualifier("googleGenAiTextEmbedding") EmbeddingModel genAiEmbeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, genAiEmbeddingModel)
                .dimensions(768)
                .build();
    }
}
