package com.whut.emall.business.vo;

import java.sql.Timestamp;

import com.whut.emall.business.entity.enums.UserStatus;

import lombok.Data;

@Data
public class UserInfo {
    Integer id;
    String username;
    String phone;
    String roleCode;
    UserStatus status;
    Timestamp createTime;
}