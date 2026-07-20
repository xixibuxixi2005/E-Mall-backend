package com.whut.emall.business.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.business.entity.Cart;
import com.whut.emall.business.entity.Order;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.mapper.OrderMapper;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.OrderItem;
import com.whut.emall.common.entity.enums.OrderStatus;
import com.whut.emall.common.utils.UniqueRamdom;
import com.whut.emall.common.vo.OrderDetailListVO;
import com.whut.emall.common.vo.OrderDetailVO;
import com.whut.emall.common.vo.OrderItemVO;
import com.whut.emall.common.vo.OrderListVO;
import com.whut.emall.common.vo.OrderVO;

import jakarta.annotation.Resource;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order>{
    @Resource OrderMapper orderMapper;
    @Resource CartService cartService;
    @Resource ProductService productService;
    @Resource OrderItemService orderItemService;
    @Resource UniqueRamdom uniqueRamdom;

    public OrderListVO orderList(Integer pageNum, Integer pageSize, String orderNo, Integer userId, OrderStatus status, Timestamp startTime, Timestamp endTime) {
        Page<OrderVO> page = orderMapper.orderList(new Page<>(pageNum, pageSize), orderNo, userId, status, startTime, endTime);
        return new OrderListVO(page);
    }

    public OrderDetailVO orderDetail(Integer uid, Integer id) {
        OrderVO order = orderMapper.selectOrderVOById(id);
        if (order == null)
            throw ApiException.err(404, "订单不存在");
        if (uid != null && !uid.equals(order.getUserId()))
            throw ApiException.err(403, "无权限查看该订单");
        List<OrderItemVO> items = orderMapper.selectItemsByOrderId(id);
        return new OrderDetailVO(order, items);
    }

    public void updateStatus(Integer id, OrderStatus status, String shippingNo) {
        Order order = orderMapper.selectById(id);
        if (order == null)
            throw ApiException.err(404, "订单不存在");
        order.setStatus(status);
        switch (status) {
            case PAID:
                if (order.getStatus() != OrderStatus.PENDING)
                    throw ApiException.err(400, " 仅能支付待支付的订单");
                order.setPayTime(new Timestamp(System.currentTimeMillis()));
                break;
            case SHIPPED:
                order.setShippingTime(new Timestamp(System.currentTimeMillis()));
                // TODO: 设置快递单号
                break;
            case FINISHED:
                order.setFinishTime(new Timestamp(System.currentTimeMillis()));
                break;
            default:
                break;
        }
        orderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Integer userId, List<Integer> cartIds, String receiverName, String receiverPhone, String receiverAddress, String remark) {
        List<Cart> carts = cartService.selectListByIdAndUserId(userId, cartIds);
        if (carts.size() != cartIds.size())
            throw ApiException.err(400, "仅能购买自己购物车中已选中的商品");
        
        List<Product> products = productService.listByIds(
            carts.stream().map(c -> c.getProductId()).toList()
        );
        List<OrderItem> orderItems = new ArrayList<>();
        Map<Integer, Product> productMap = new HashMap<>();
        products.forEach(p -> productMap.put(p.getId(), p));
        for (var cart: carts) {
            Product product = productMap.get(cart.getProductId());
            if (cart.getQuantity() > product.getStock()) {
                throw ApiException.err(400, "商品库存量不足：商品id=" + cart.getProductId());
            } else {
                product.setStock(product.getStock()-cart.getQuantity());
                OrderItem item = new OrderItem();
                item.setProductId(cart.getProductId());
                item.setQuantity(cart.getQuantity());
                orderItems.add(item);
            }
        }
        
        Order order = new Order();
        order.setOrderNo(uniqueRamdom.getUniqueDateLabel(5, "ORD"));
        order.setUserId(userId);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(remark);
        orderMapper.insert(order);
        cartService.remove(userId, cartIds);
        productService.updateBatchById(products);
        Integer orderId = order.getId();
        orderItems.forEach(item -> item.setOrderId(orderId));
        orderItemService.saveBatch(orderItems);
        return getById(orderId);
    }

    public OrderDetailListVO myOrders(Integer pageNum, Integer pageSize, Integer uid, OrderStatus status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, uid).eq(status != null, Order::getStatus, status);
        Page<Order> result = page(new Page<>(pageNum, pageSize), wrapper);
        OrderDetailListVO vo = new OrderDetailListVO();
        List<OrderDetailVO> list = new ArrayList<>();
        vo.setTotal(result.getTotal());
        for (Order order: result.getRecords()) {
            list.add(orderDetail(uid, order.getId()));
        }
        vo.setList(list);
        return vo;
    }

    public void pay(Integer userId, Integer id, BigDecimal amount) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, id).eq(Order::getUserId, userId);
        Order order = getOne(wrapper);
        if (order == null)
            throw ApiException.err(404, "订单不存在");
        if (order.getStatus() != OrderStatus.PENDING)
            throw ApiException.err(400, " 仅能支付待支付的订单");
        order.setPayAmount(amount.add(order.getPayAmount()));
        order.setPayTime(new Timestamp(System.currentTimeMillis()));
        BigDecimal totalAmount = orderDetail(userId, id).getTotalAmount();
        if (order.getPayAmount().compareTo(totalAmount) != -1)
            order.setStatus(OrderStatus.PAID);
        updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer userId, Integer id, String reason) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, id).eq(Order::getUserId, userId);
        Order order = getOne(wrapper);
        if (order == null)
            throw ApiException.err(404, "订单不存在");
        if (order.getStatus() != OrderStatus.PENDING)
            throw ApiException.err(400, " 仅能取消待支付的订单");
        List<OrderItem> items = orderItemService.getByOrderId(order.getId());
        for(var item: items) {
            Product product = productService.getById(item.getProductId());
            product.setStock(product.getStock()+item.getQuantity());
            productService.updateById(product);
        }
        order.setStatus(OrderStatus.CANCELED);
        // TODO: 保存取消原因
        updateById(order);
    }
}
