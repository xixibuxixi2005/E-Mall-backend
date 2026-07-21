package com.whut.emall.ai.vo;

import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@Data
public class UserProfileVO {
    @JsonPropertyDescription("用户Id")
    private Integer userId;
    private List<Label> labels;

    @Data
    public static class Label {
        @JsonPropertyDescription("标签名")
        private String name;
        @JsonPropertyDescription("置信度，从0到1")
        private Double confidence;
    }

}
