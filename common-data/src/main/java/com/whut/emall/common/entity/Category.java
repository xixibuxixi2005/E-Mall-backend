package com.whut.emall.common.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("category")
public class Category {
    Integer id;
    String name;
    Integer parentId;
    Integer level;
    Integer sortOrder;
    Timestamp createTime;
}
