package com.whut.emall.common.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartListVO {
    List<CartDetailVO> items;
    BigDecimal totalPrice;
    Integer totalQuantity;
}
