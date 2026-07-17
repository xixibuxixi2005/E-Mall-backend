package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionMode implements IEmallEnum{
    CS(0, "真人客服"),
    AI(1, "AI自动回复");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
