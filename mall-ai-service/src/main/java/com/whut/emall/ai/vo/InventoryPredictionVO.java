package com.whut.emall.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class InventoryPredictionVO {
    private Integer productId;
    private String productName;
    private Integer predictedSales; //预测销量
    private Integer currentStock;  //当前库存
    private Integer suggestedRestock; //建议补货量
    private List<Integer> confidenceInterval;
    private List<DailyForecast> forecast;

    @Data
    public static class DailyForecast {
        private String date;
        private String sales;
    }
}




