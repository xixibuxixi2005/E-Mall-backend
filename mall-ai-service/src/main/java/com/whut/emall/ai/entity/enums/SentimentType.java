package com.whut.emall.ai.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SentimentType implements IEmallEnum{
    NEGATIVE(0, "负面"),
    NEUTRAL(1, "中性"),
    POSITIVE(2, "正面");

    @EnumValue final Integer value;
    @JsonValue final String desc;
}
