package com.devforgely.aimanusbackend.agents;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AiManusTest {
    @Resource
    private AiManus aiManus;

    @Test
    public void run() {
        String userPrompt = """
               Please help me plan a detailed meetup with my friend who lives in London.
               Look for a great meeting area that combines a nice atmosphere with good amenities.
               Also, please include some online images about the location,
               and compile all gathered information into a PDF format as output.""";
        String answer = aiManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
