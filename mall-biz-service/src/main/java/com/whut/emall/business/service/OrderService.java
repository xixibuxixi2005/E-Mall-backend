package com.whut.emall.business.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.business.entity.Cart;
import com.whut.emall.business.entity.Order;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.entity.enums.OrderStatus;
import com.whut.emall.business.mapper.OrderMapper;
import com.whut.emall.business.vo.OrderDetailVO;
import com.whut.emall.business.vo.OrderItemVO;
import com.whut.emall.business.vo.OrderListVO;
import com.whut.emall.business.vo.OrderVO;
import com.whut.emall.common.entity.ApiException;

import jakarta.annotation.Resource;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order>{
    @Resource OrderMapper orderMapper;
    @Resource CartService cartService;
    @Resource ProductService productService;

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
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setItems(items);
        return vo;
    }

    public void updateStatus(Integer id, OrderStatus status, String shippingNo) {
        Order order = orderMapper.selectById(id);
        if (order == null)
            throw ApiException.err(404, "订单不存在");
        order.setStatus(status);
        switch (status) {
            case PAID:
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

    final Random random = new Random();
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Integer userId, List<Integer> cartIds, String receiverName, String receiverPhone, String receiverAddress, String remark) {
        List<Cart> carts = cartService.selectListByIdAndUserId(userId, cartIds);
        if (carts.size() != cartIds.size())
            throw ApiException.err(400, "仅能购买自己购物车中已选中的商品");
        
        List<Product> products = productService.listByIds(
            carts.stream().map(c -> c.getProductId()).toList()
        );
        Map<Integer, Product> productMap = new HashMap<>();
        products.forEach(p -> productMap.put(p.getId(), p));
        for (var cart: carts) {
            Product product = productMap.get(cart.getProductId());
            if (cart.getQuantity() > product.getStock())
                throw ApiException.err(400, "商品库存量不足：商品id=" + cart.getProductId());
            else
                product.setStock(product.getStock()-cart.getQuantity());
        }
        
        Order order = new Order();
        order.setOrderNo(String.format("ORD%010d", random.nextLong(10000000000l))); //  TODO:生成订单号
        order.setUserId(userId);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(remark);
        orderMapper.insert(order);
        cartService.remove(userId, cartIds);
        productService.updateBatchById(products);
        return getById(order.getId());
    }
}
