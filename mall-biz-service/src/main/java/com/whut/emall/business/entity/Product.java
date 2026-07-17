package com.whut.emall.business.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.whut.emall.common.entity.enums.ProductStatus;

import lombok.Data;

@Data
@TableName(value = "product", autoResultMap = true)
public class Product {
    Integer id;
    String name;
    String subTitle;
    Integer categoryId;
    BigDecimal price;
    BigDecimal originalPrice;
    Integer stock;
    ProductStatus status;
    String description;

    @TableField(typeHandler = JacksonTypeHandler.class)
    List<String> imageUrls;

    Timestamp createTime;
    Timestamp updateTime;
}
