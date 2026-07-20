package com.whut.emall.common.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderListVO {
    List<OrderVO> list;
    Long total;

    public OrderListVO(Page<OrderVO> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
    }
}
