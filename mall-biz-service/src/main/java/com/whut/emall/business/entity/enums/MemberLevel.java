package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberLevel implements IEmallEnum{
    NORMAL(1, "普通会员"),
    SIVLER(2, "白银会员"),
    GOLD(3, "黄金会员");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
