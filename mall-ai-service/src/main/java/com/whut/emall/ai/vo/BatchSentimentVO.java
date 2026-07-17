package com.whut.emall.ai.vo;

import java.util.List;

import com.whut.emall.ai.entity.CommentSentiment;

public record BatchSentimentVO(
    List<CommentSentiment> results,
    int positive,
    int neutral,
    int negative
) {}