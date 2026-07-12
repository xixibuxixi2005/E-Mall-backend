package com.whut.emall.business.vo;

import java.util.List;

import lombok.Data;

@Data
public class OrderDetailVO {
    OrderVO order;
    List<OrderItemVO> items;
}
