package com.whut.emall.ai.vo;

import lombok.Data;

@Data
public class RecommendVO {
    Integer productId;
    String name;
    Double price;
    String imageUrl;
    String reason;
    Double score;
}