package com.whut.emall.ai.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.whut.emall.ai.entity.CommentSentiment;

public record BatchSentimentVO(
    @JsonPropertyDescription("评论情感分析结果列表，元素的sentiment字段必须为中文：负面、中性、正面")
    List<CommentSentiment> results,
    int positive,
    int neutral,
    int negative
) {}