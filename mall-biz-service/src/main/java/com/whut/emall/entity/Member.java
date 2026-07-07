package com.whut.emall.entity;

import java.sql.Date;

import lombok.Data;

@Data
public class Member {
    Integer id;
    String username;
    String password;
    String phone;
    String email;
    String level;
    Integer points;
    String status;
    Date create_time;
    Date update_time;
}
