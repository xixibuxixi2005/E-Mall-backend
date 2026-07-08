package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("member")
public class Member {
    Long id;
    String email;
    String password;
    String username;
    String phone;
    Integer level;
    Integer points;
    Integer status;
    Timestamp createTime;
    Timestamp updateTime;
}
