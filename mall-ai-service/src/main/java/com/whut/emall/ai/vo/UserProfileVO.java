package com.whut.emall.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileVO {
    private Long userId;
    private List<Label> labels;

    @Data
    public static class Label {
        private String name;
        private Double confidence;
    }

}
