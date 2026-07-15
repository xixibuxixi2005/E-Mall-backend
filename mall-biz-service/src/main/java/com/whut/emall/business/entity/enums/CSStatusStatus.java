package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CSStatusStatus implements IEmallEnum{
    OFFLINE(0, "离线"),
    ONLINE(1, "在线"),
    BUSY(2, "忙碌");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}