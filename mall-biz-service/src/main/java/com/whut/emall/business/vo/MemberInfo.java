package com.whut.emall.business.vo;

import java.sql.Timestamp;

import com.whut.emall.business.entity.enums.MemberLevel;

import lombok.Data;

@Data
public class MemberInfo {
    Integer id;
    String username;
    String email;
    String phone;
    MemberLevel level;
    Integer points;
    Timestamp createTime;
}