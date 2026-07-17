package com.whut.emall.ai.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.whut.emall.ai.entity.enums.SentimentType;

public record CommentSentiment(
    @JsonPropertyDescription("评论情感类型，传入必须是以下中文之一：负面、中性、正面")
    SentimentType sentiment,
    @JsonPropertyDescription("评论类型为该类型的置信度")
    Double score,
    @JsonPropertyDescription("评论中提取的所有关键词")
    List<String> keywords,
    @JsonPropertyDescription("评论中提取的正面词")
    List<String> positiveWords,
    @JsonPropertyDescription("评论中提取的负面词")
    List<String> negativeWords
) {}