package com.whut.emall.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.mapper.OrderItemMapper;
import com.whut.emall.business.mapper.OrderMapper;
import com.whut.emall.business.service.OrderItemService;
import com.whut.emall.business.service.OrderService;
import com.whut.emall.common.entity.OrderItem;
import com.whut.emall.common.entity.enums.OrderStatus;
import com.whut.emall.common.vo.OrderDetailVO;
import com.whut.emall.common.vo.OrderListVO;
import com.whut.emall.common.vo.OrderVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class OrderServiceTest {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderService orderService;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private OrderItemService orderItemService;

    private static Integer testOrderId;

    @Test
    @Order(1)
    public void testCreateOrder() {
        com.whut.emall.business.entity.Order order = new com.whut.emall.business.entity.Order();
        order.setOrderNo("TEST20260723001");
        order.setUserId(1);
        order.setPayAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatus.PENDING);
        int rows = orderMapper.insert(order);
        testOrderId = order.getId();
        Assertions.assertEquals(1, rows);
        System.out.println("创建订单ID：" + testOrderId);
    }

    @Test
    @Order(2)
    public void testSelectOrderVOById() {
        if (testOrderId == null) {
            var o = orderMapper.selectOne(new LambdaQueryWrapper<com.whut.emall.business.entity.Order>().last("LIMIT 1"));
            if (o != null) testOrderId = o.getId();
        }
        Assertions.assertNotNull(testOrderId, "没有可测试的订单");
        OrderVO vo = orderMapper.selectOrderVOById(testOrderId);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getOrderNo());
        System.out.println("订单号：" + vo.getOrderNo());
    }

    @Test
    @Order(3)
    public void testOrderList() {
        OrderListVO vo = orderService.orderList(1, 10, null, null, null, null, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
        System.out.println("订单总数：" + vo.getTotal());
    }

    @Test
    @Order(4)
    public void testOrderListWithFilter() {
        Page<OrderVO> page = orderMapper.orderList(new Page<>(1, 10), null, 1, OrderStatus.PENDING, null, null);
        Assertions.assertNotNull(page);
        System.out.println("待支付订单数：" + page.getTotal());
    }

    @Test
    @Order(5)
    public void testOrderDetail() {
        if (testOrderId == null) return;
        OrderDetailVO detail = orderService.orderDetail(null, testOrderId);
        Assertions.assertNotNull(detail);
        Assertions.assertNotNull(detail.getOrderNo());
    }

    @Test
    @Order(6)
    public void testSelectItemsByOrderId() {
        if (testOrderId == null) return;
        var items = orderMapper.selectItemsByOrderId(testOrderId);
        Assertions.assertNotNull(items);
        System.out.println("订单项数：" + items.size());
    }

    @Test
    @Order(7)
    public void testInsertOrderItem() {
        if (testOrderId == null) return;
        OrderItem item = new OrderItem();
        item.setOrderId(testOrderId);
        item.setProductId(1);
        item.setQuantity(2);
        int rows = orderItemMapper.insert(item);
        Assertions.assertEquals(1, rows);
        List<OrderItem> items = orderItemService.getByOrderId(testOrderId);
        Assertions.assertFalse(items.isEmpty());
    }

    @Test
    @Order(8)
    public void testUpdateOrderStatus() {
        if (testOrderId == null) return;
        orderService.updateStatus(testOrderId, OrderStatus.PAID, null);
        var order = orderMapper.selectById(testOrderId);
        Assertions.assertEquals(OrderStatus.PAID, order.getStatus());
        Assertions.assertNotNull(order.getPayTime());
    }

    @Test
    @Order(9)
    public void testMyOrders() {
        var vo = orderService.myOrders(1, 10, 1, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
    }

    @Test
    @Order(10)
    public void testOrderListByTimeRange() {
        Timestamp startTime = Timestamp.valueOf("2020-01-01 00:00:00");
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        Page<OrderVO> page = orderMapper.orderList(new Page<>(1, 10), null, null, null, startTime, endTime);
        Assertions.assertNotNull(page);
    }

    @Test
    @Order(11)
    public void testCancelOrder() {
        if (testOrderId == null) return;
        var order = orderMapper.selectById(testOrderId);
        if (order != null) {
            order.setStatus(OrderStatus.PENDING);
            orderMapper.updateById(order);
        }
        Assertions.assertDoesNotThrow(() -> {
            orderService.cancel(1, testOrderId, "测试取消");
        });
        var canceled = orderMapper.selectById(testOrderId);
        Assertions.assertEquals(OrderStatus.CANCELED, canceled.getStatus());
    }

    @Test
    @Order(12)
    public void testDeleteOrder() {
        if (testOrderId == null) return;
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, testOrderId));
        orderMapper.deleteById(testOrderId);
        var deleted = orderMapper.selectById(testOrderId);
        Assertions.assertNull(deleted);
        System.out.println("删除订单ID：" + testOrderId);
    }
}
