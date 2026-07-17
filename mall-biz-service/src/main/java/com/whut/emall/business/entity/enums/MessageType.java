package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType implements IEmallEnum{
    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    PRODUCT(2, "商品卡片"),
    ORDER(3, "订单卡片");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
