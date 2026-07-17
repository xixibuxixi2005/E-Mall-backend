package com.whut.emall.ai.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.whut.emall.ai.client.ChatClient;

import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    @Resource LLMService llmService;
    @Resource VectorStore vectorStore;

    public Flux<String> streamChat(String question, Integer sessionId, Integer topK) {
        // TODO: 支持特定会话上下文回答
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder().query(question).topK(topK).build()
        );
        return llmService.streamSimilarChar(question, docs);
    }
}
