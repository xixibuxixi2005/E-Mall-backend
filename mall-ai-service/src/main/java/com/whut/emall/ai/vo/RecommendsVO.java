package com.whut.emall.ai.vo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record RecommendsVO(
    @JsonPropertyDescription("商品ID")
    Integer productId,
    @JsonPropertyDescription("商品名称")
    String name,
    @JsonPropertyDescription("商品价格")
    Double price,
    @JsonPropertyDescription("商品图片URL")
    String imageUrl,
    @JsonPropertyDescription("推荐理由")
    String reason,
    @JsonPropertyDescription("推荐分数，范围从0到1，表示该商品被推荐的可能性")
    Double score
) {}