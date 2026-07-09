package com.whut.emall.business.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberLevel {
    NORMAL(1, "普通"),
    SIVLER(2, "白银"),
    GOLD(3, "黄金");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
