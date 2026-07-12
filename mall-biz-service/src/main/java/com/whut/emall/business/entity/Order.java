package com.whut.emall.business.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;
import com.whut.emall.business.entity.enums.OrderStatus;

import lombok.Data;

@Data
@TableName("order")
public class Order {
    Integer id;
    String orderNo;
    Integer userId;
    BigDecimal totalAmount;
    BigDecimal payAmount;
    OrderStatus status;
    Timestamp payTime;
    Timestamp shippingTime;
    Timestamp finishTime;
    String receiverName;
    String receiverPhone;
    String receiverAddress;
    String remark;
    Timestamp createTime;
    Timestamp updateTime;
}
