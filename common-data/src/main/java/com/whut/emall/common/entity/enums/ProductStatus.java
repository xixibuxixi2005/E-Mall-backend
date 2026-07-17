package com.whut.emall.common.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus implements IEmallEnum{
    ON_SALE(1, "上架"),
    OFF_SALE(0, "下架");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
