package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("cart")
public class Cart {
    Integer id;
    Integer userId;
    Integer productId;
    Integer quantity;
    Boolean selected;
    Timestamp createTime;
}
