package com.whut.emall.ai.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.ai.client.BizClient;
import com.whut.emall.ai.tools.AITools;
import com.whut.emall.ai.vo.AISuggestVO;
import com.whut.emall.common.vo.ChatMessageListVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class ChatService {
    @Resource LLMService llmService;
    @Resource VectorStore vectorStore;
    @Resource BizClient bizClient;
    @Resource AITools aiTools;
    final ObjectMapper objectMapper = new ObjectMapper();

    public Flux<String> streamChat(String question, Integer sessionId, Integer topK) {
        if (sessionId != null) {
            ChatMessageListVO messages = aiTools.getChatMessages(1, 0, sessionId);
            question = "(会话id=" + sessionId + "，会话总长度=" + messages.getTotal() + ") \n" + question;
        }
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder().query(question).topK(topK).build()
        );
        return llmService.streamSimilarChar(question, docs, aiTools);
    }

    public AISuggestVO csAiSuggest(Integer csId, Integer sessionId, String userQuestion) {
        if (sessionId != null) {
            ChatMessageListVO messages = aiTools.getChatMessages(1, -1, sessionId);
            try {
                userQuestion = "(会话上下文JSON:" + objectMapper.writeValueAsString(messages.getList()) + ") \n" + userQuestion;
            } catch (Exception e) {
                log.error("转换会话上下文JSON失败", e);
            }
        }
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder().query(userQuestion).topK(10).build()
        );
        StringBuffer promptBuilder = new StringBuffer();
        promptBuilder.append("你是一个客服AI建议助理，根据可能的参考资料和商品信息给出客服回复建议。若根据现有资料和数据库商品信息无法回答，回复“根据知识库内容无法回答”。\n");
        var referrence = llmService.docsToPrompt(docs, 0.5);
        if (!referrence.strip().isEmpty())
            promptBuilder.append("参考资料：").append(referrence);
        String prompt = promptBuilder.toString();
        return llmService.customPromptStructCall(prompt, userQuestion, AISuggestVO.class, aiTools);
    }
}
