package com.whut.emall.common.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("order_item")
public class OrderItem {
    Integer id;
    Integer orderId;
    Integer productId;
    Integer quantity;
    Timestamp createTime;
}
