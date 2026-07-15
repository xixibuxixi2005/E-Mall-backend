package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SenderType implements IEmallEnum{
    USER(0, "用户"),
    CS(1, "客服"),
    AI(2, "AI机器人");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
