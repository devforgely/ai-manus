package com.devforgely.aimanusbackend.app;

import com.devforgely.aimanusbackend.advisors.LoggerAdvisor;
import com.devforgely.aimanusbackend.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class FocusApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "You are a procrastination coach trained " +
            "in evidence-based psychological research and behavioral science. Guide users to describe the current situation " +
            "then provide a unique solution to solve the user's issue about procrastination.";

    public FocusApp(@Qualifier("googleGenAiChatModel") ChatModel aiChatModel) {
        // initialise filed based chat memory
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // initialise chat memory in RAM
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
        chatClient = ChatClient.builder(aiChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        // custom advisor, use if required
//                        new LoggerAdvisor(),
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * Ai Chat supported with consecutive chats
     * @param message is the user query
     * @param chatId is the id for current chat
     * @return String of the result
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * Ai Chat supported with consecutive chats, sse
     * @param message is the user query
     * @param chatId  is the id for current chat
     * @return String of the result
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    public record FocusReport(String title, List<String> suggestions) { }

    /**
     * AI report, structured output
     * @param message is the user query
     * @param chatId is the id for current chat
     * @return record type of the result
     */
    public FocusReport doChatWithReport(String message, String chatId) {
        FocusReport focusReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "On every conversation, " +
                        "generate a focus report title {username}'s focus report, and the content is a list of suggestions.")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(FocusReport.class);
        log.info("focusReport: {}", focusReport);
        return focusReport;
    }

    /**
     * AI RAG Q&A Functions
     */
    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * Use RAG for chat
     */
    public String doChatWithRag(String message, String chatId) {
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // use logger for inspection
                .advisors(new LoggerAdvisor())
                // use RAG Q&A and RAG augmentation（PgVector based）
                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                // use custom RAG augmentation（document search + context augmentation）
//                .advisors(
//                        focusAppRagCustomAdvisorFactory.createFocusAppRagCustomAdvisor(
//                                focusAppVectorStore, "Part 1"
//                        )
//                )
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content with RAG: {}", content);
        return content;
    }

    // AI tool calling
    @Resource
    private ToolCallback[] allTools;

    /**
     * AI report (supported with tool calls)
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // use logger for inspection
                .advisors(new LoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        assert chatResponse != null;
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content with tools: {}", content);
        return content;
    }
}
