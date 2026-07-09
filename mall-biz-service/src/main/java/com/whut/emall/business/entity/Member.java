package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;
import com.whut.emall.business.entity.enums.MemberLevel;
import com.whut.emall.business.entity.enums.UserStatus;

import lombok.Data;

@Data
@TableName("member")
public class Member {
    Long id;
    String email;
    String password;
    String username;
    String phone;
    MemberLevel level;
    Integer points;
    UserStatus status;
    Timestamp createTime;
    Timestamp updateTime;
}
