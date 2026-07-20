package com.whut.emall.common.vo;

import java.sql.Timestamp;

import com.whut.emall.common.entity.enums.MemberLevel;

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
    // TODO: 添加avatar字段
}