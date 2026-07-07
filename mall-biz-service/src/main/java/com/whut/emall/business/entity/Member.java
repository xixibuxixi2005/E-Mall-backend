package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@TableName("member")
public class Member {
    Long id;
    String email;
    String password;
    String nickname;
    String phone;
    String level;
    Integer points;
    String status;
    @JsonProperty("create_time")
    Timestamp createTime;
    @JsonProperty("update_time")
    Timestamp updateTime;
}
