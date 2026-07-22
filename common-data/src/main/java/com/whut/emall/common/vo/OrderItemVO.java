package com.whut.emall.common.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemVO {
    Integer id;
    Integer productId;
    String productName;
    String imageUrl;
    BigDecimal productPrice;
    Integer quantity;
    BigDecimal totalPrice;
}
