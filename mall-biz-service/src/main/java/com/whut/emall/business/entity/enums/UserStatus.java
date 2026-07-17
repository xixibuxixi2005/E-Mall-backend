package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whut.emall.common.entity.enums.IEmallEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus implements IEmallEnum{
    VALID(1, "有效"),
    INVALID(0, "禁用");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
