package com.whut.emall.business.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class CartDetailVO {
    Integer cartId;
    Integer productId;
    String productName;
    BigDecimal productPrice;
    BigDecimal originalPrice;
    String productImage;
    Integer quantity;
    Integer stock;
    Boolean selected;
    Timestamp createTime;
}
