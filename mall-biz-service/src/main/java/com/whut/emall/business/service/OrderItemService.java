package com.whut.emall.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.business.entity.OrderItem;
import com.whut.emall.business.mapper.OrderItemMapper;

@Service
public class OrderItemService extends ServiceImpl<OrderItemMapper, OrderItem>{
    public List<OrderItem> getByOrderId(Integer orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return list(wrapper);
    }
}
