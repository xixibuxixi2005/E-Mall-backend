package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whut.emall.common.entity.enums.IEmallEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionStatus implements IEmallEnum{
    WAITING(0, "排队中"),
    SERVING(1, "服务中"),
    FINISHED(2, "已结束"),
    AI(3, "AI托管");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}