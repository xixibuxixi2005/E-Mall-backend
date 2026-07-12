package com.whut.emall.business.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.enums.OrderStatus;
import com.whut.emall.business.mapper.OrderMapper;
import com.whut.emall.business.vo.OrderDetailVO;
import com.whut.emall.business.vo.OrderItemVO;
import com.whut.emall.business.vo.OrderListVO;
import com.whut.emall.business.vo.OrderVO;

import jakarta.annotation.Resource;

@Service
public class OrderService {
    @Resource OrderMapper orderMapper;

    public OrderListVO orderList(Integer pageNum, Integer pageSize, String orderNo, Integer userId, OrderStatus status, Timestamp startTime, Timestamp endTime) {
        Page<OrderVO> page = orderMapper.orderList(new Page<>(pageNum, pageSize), orderNo, userId, status, startTime, endTime);
        return new OrderListVO(page);
    }

    public OrderDetailVO orderDetail(Integer uid, Integer id) {
        OrderVO order = orderMapper.selectOrderVOById(id);
        if (order == null)
            throw new RuntimeException("订单不存在");
        if (uid != null && !uid.equals(order.getUserId()))
            throw new RuntimeException("无权限查看该订单");
        List<OrderItemVO> items = orderMapper.selectItemsByOrderId(id);
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setItems(items);
        return vo;

    }
}
