package com.devforgely.aimanusbackend.demo.rag;

import com.devforgely.aimanusbackend.rag.FocusAppDocumentLoader;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

//@Component
public class FocusAppVectorStoreInitialiser implements CommandLineRunner {
    @Resource
    private FocusAppDocumentLoader focusAppDocumentLoader;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // Check if already populated
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_store", Integer.class);

        if (count != null && count > 0) {
            System.out.println("Vector store already initialized. Skipping document ingestion.");
            return;
        }

        System.out.println("Populating vector store for the first time...");

        List<Document> documents = focusAppDocumentLoader.loadMarkdowns();
        pgVectorVectorStore.add(documents);

        System.out.println("Vector store ingestion completed.");
    }
}
