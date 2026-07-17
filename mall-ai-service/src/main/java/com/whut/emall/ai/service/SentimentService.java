package com.whut.emall.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.whut.emall.ai.entity.CommentSentiment;
import com.whut.emall.ai.vo.BatchSentimentVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SentimentService {
    @Resource LLMService llmService;

    public CommentSentiment analyze(String comment) {
        String prompt = """
你是一个评论情感分析助理，分析用户评论的情感类型和关键词。请根据以下要求进行分析并以JSON格式返回。
示例："手机性能很强，就是价格有点贵，但物流很快。"
响应示例：{"sentiment": "正面", "score": 0.75, "keywords": ["性能强", "价格贵", "物流快"], "positiveWords": ["性能强", "物流快"], "negativeWords": ["价格贵"]}
        """;
        return llmService.customPromptStructCall(prompt, comment, CommentSentiment.class);
    }
    public BatchSentimentVO analyze(List<String> comment) {
        String prompt = """
你是一个评论情感分析助理，分析用户评论的情感类型和关键词。请分析每一条评论并以json数组形式返回。
示例：["手机性能很强，就是价格有点贵，但物流很快。"]
响应示例：[{"sentiment": "正面", "score": 0.75, "keywords": ["性能强", "价格贵", "物流快"], "positiveWords": ["性能强", "物流快"], "negativeWords": ["价格贵"]}]
        """;
        StringBuilder userInputs = new StringBuilder();
        for (int i=0 ; i<comment.size() ; ++i) {
            userInputs.append(String.format("评论%d：%s\n", i+1, comment.get(i)));
        }
        return llmService.customPromptStructCall(prompt, userInputs.toString(), BatchSentimentVO.class);
    }
}