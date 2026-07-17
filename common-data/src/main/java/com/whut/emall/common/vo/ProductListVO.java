package com.whut.emall.common.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

@Data
public class ProductListVO {
    List<ProductDetailVO> list;
    Long total;

    public ProductListVO(Page<ProductDetailVO> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
    }
}
