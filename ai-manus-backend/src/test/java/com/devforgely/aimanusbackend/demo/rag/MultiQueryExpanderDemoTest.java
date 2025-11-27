package com.devforgely.aimanusbackend.demo.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.ai.rag.Query;
import java.util.List;

@SpringBootTest
public class MultiQueryExpanderDemoTest {
    @Resource
    private MultiQueryExpanderDemo multiQueryExpanderDemo;

    @Test
    void expand() {
        List<Query> queries = multiQueryExpanderDemo.expand("What is Spring AI haha.");
        System.out.println(queries);
        Assertions.assertNotNull(queries);
    }
}
