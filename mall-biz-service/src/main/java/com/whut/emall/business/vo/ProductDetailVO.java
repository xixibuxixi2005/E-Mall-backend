package com.whut.emall.business.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.whut.emall.business.entity.enums.ProductStatus;

import lombok.Data;

@Data
public class ProductDetailVO {
    Integer id;
    String name;
    String subTitle;
    Integer categoryId;
    String categoryName;
    BigDecimal price;
    BigDecimal originalPrice;
    Integer stock;
    ProductStatus status;
    String description;
    List<String> imageUrls;
    Timestamp createTime;
    Timestamp updateTime;
}
