package com.whut.emall.business.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

@Data
public class OrderListVO {
    List<OrderVO> list;
    Long total;

    public OrderListVO(Page<OrderVO> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
    }
}
