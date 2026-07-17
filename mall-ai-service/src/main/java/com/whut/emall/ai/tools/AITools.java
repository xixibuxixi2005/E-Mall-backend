package com.whut.emall.ai.tools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whut.emall.ai.client.BizClient;
import com.whut.emall.ai.mapper.OrderItemMapper;
import com.whut.emall.common.entity.ApiResult;
import com.whut.emall.common.entity.Category;
import com.whut.emall.common.entity.OrderItem;
import com.whut.emall.common.entity.enums.OrderStatus;
import com.whut.emall.common.entity.enums.ProductStatus;
import com.whut.emall.common.vo.CartListVO;
import com.whut.emall.common.vo.OrderDetailListVO;
import com.whut.emall.common.vo.ProductDetailVO;
import com.whut.emall.common.vo.ProductListVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AITools {
    @Resource BizClient bizClient;
    @Resource OrderItemMapper orderItemMapper;

    @Tool(description = "获取当前时间日期")
    public LocalDate getCurrentDate() {
        log.debug("获取当前时间日期");
        return LocalDate.now();
    }

    @Tool(description = "根据商品ID获取商品详情")
    public ProductDetailVO getProductDetail(Integer id) {
        log.debug("获取商品详情: id={}", id);
        ApiResult<ProductDetailVO> result = bizClient.getProductDetail(id);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据用户ID获取购物车列表")
    public CartListVO getCartList(Integer userId) {
        log.debug("获取购物车列表: userId={}", userId);
        ApiResult<CartListVO> result = bizClient.cartList(userId);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据用户ID获取个人订单列表")
    public OrderDetailListVO getMyOrders(Integer userId, Integer pageNum, Integer pageSize, OrderStatus status) {
        log.debug("获取个人订单列表: userId={}, pageNum={}, pageSize={}, status={}", userId, pageNum, pageSize, status);
        ApiResult<OrderDetailListVO> result = bizClient.myOrders(userId, pageNum, pageSize, status);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据用户ID获取会员资料")
    public com.whut.emall.common.vo.MemberInfo getMemberInfo(Integer userId) {
        log.debug("获取会员资料: userId={}", userId);
        ApiResult<com.whut.emall.common.vo.MemberInfo> result = bizClient.getMemberInfo(userId);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据商品名称、状态和价格区间获取商品列表")
    public ProductListVO getProductList(Integer pageNum, Integer pageSize, String name, ProductStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("获取商品列表: pageNum={}, pageSize={}, name={}, status={}, minPrice={}, maxPrice={}", pageNum, pageSize, name, status, minPrice, maxPrice);
        ApiResult<ProductListVO> result = bizClient.productList(pageNum, pageSize, name, status, minPrice, maxPrice);
        return result == null ? null : result.getData();
    }

    @Tool(description = "获取所有商品分类")
    public List<Category> getCategories() {
        log.debug("获取所有商品分类");
        ApiResult<List<Category>> result = bizClient.getCategories();
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据订单ID、商品ID等信息获取订单项列表（为null则匹配全部）")
    public List<OrderItem> getOrderItems(Integer orderId, Integer productId, LocalDate startDate, LocalDate endDate) {
        log.debug("获取订单项列表: orderId={}, productId={}, startDate={}, endDate={}", orderId, productId, startDate, endDate);
        List<OrderItem> result = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
            .eq(orderId != null, OrderItem::getOrderId, orderId)
            .eq(productId != null, OrderItem::getProductId, productId)
            .ge(startDate != null, OrderItem::getCreateTime, startDate)
            .le(endDate != null, OrderItem::getCreateTime, endDate));
        return result;
    }
}
