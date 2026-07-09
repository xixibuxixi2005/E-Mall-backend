package com.whut.emall.business.vo;

import com.whut.emall.business.entity.enums.MemberLevel;

import lombok.Data;

@Data
public class MemberInfo {
    Integer id;
    String username;
    String phone;
    MemberLevel level;
    Integer points;
}