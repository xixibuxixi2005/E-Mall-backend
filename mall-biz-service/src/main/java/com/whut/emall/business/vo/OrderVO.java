package com.whut.emall.business.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.whut.emall.business.entity.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderVO {
    Integer id;
    String orderNo;
    Integer userId;
    String username;
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
