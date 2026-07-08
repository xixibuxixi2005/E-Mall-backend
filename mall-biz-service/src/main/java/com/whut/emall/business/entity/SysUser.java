package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("sys_user")
public class SysUser {
    Long id;
    String email;
    String password;
    String username;
    String phone;
    String roleCode;
    Integer status;
    Timestamp createTime;
    Timestamp updateTime;
}
