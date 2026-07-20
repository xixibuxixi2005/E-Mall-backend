package com.whut.emall.common.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemVO {
    Integer id;
    Integer productId;
    String productName;
    BigDecimal productPrice;
    Integer quantity;
    BigDecimal totalPrice;
}
