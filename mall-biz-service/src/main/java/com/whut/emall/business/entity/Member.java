package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.common.entity.enums.MemberLevel;

import lombok.Data;

@Data
@TableName("member")
public class Member {
    Integer id;
    String email;
    String password;
    String username;
    String phone;
    MemberLevel level;
    Integer points;
    UserStatus status;
    Timestamp createTime;
    Timestamp updateTime;
    // TODO: 添加avatar字段
}
