package com.whut.emall.common.vo;

import java.util.List;

import lombok.Data;
@Data
public class OrderDetailListVO {
    List<OrderDetailVO> list;
    Long total;
}
