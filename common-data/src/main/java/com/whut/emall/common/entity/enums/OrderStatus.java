package com.whut.emall.common.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus implements IEmallEnum {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    FINISHED(3, "已完成"),
    CANCELED(4, "已取消"),
    REFUND(5, "退款中");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
