package com.whut.emall.common.vo;

import java.util.List;

import lombok.Getter;

@Getter
public class OrderDetailVO extends OrderVO{
    List<OrderItemVO> items;
    public OrderDetailVO(OrderVO order, List<OrderItemVO> items) {
        this.id = order.id;
        this.orderNo = order.orderNo;
        this.userId = order.userId;
        this.username = order.username;
        this.totalAmount = order.totalAmount;
        this.payAmount = order.payAmount;
        this.status = order.status;
        this.payTime = order.payTime;
        this.shippingTime = order.shippingTime;
        this.finishTime = order.finishTime;
        this.receiverName = order.receiverName;
        this.receiverPhone = order.receiverPhone;
        this.receiverAddress = order.receiverAddress;
        this.remark = order.remark;
        this.createTime = order.createTime;
        this.updateTime = order.updateTime;
        this.items = items;
    }
}
