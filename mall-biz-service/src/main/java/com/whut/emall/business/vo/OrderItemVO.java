package com.whut.emall.business.vo;

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
