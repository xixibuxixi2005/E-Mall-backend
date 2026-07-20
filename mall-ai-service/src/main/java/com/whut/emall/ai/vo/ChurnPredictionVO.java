package com.whut.emall.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChurnPredictionVO {

    private List<HighRiskUser> highRiskUsers;
    private Integer totalAnalyzed;

    @Data
    public static class HighRiskUser {
        private Integer userId;
        private Double riskScore;
        private String reason;
    }
}
