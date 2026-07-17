package com.whut.emall.ai.tools;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.whut.emall.ai.client.BizClient;
import com.whut.emall.common.entity.ApiResult;
import com.whut.emall.common.entity.Category;
import com.whut.emall.common.entity.enums.OrderStatus;
import com.whut.emall.common.entity.enums.ProductStatus;
import com.whut.emall.common.vo.CartListVO;
import com.whut.emall.common.vo.OrderDetailListVO;
import com.whut.emall.common.vo.ProductDetailVO;
import com.whut.emall.common.vo.ProductListVO;

import jakarta.annotation.Resource;

@Component
public class AITools {
    @Resource BizClient bizClient;

    @Tool(description = "根据商品ID获取商品详情")
    public ProductDetailVO getProductDetail(Integer id) {
        ApiResult<ProductDetailVO> result = bizClient.getProductDetail(id);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据用户ID获取购物车列表")
    public CartListVO getCartList(Integer userId) {
        ApiResult<CartListVO> result = bizClient.cartList(userId);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据用户ID获取个人订单列表")
    public OrderDetailListVO getMyOrders(Integer userId, Integer pageNum, Integer pageSize, OrderStatus status) {
        ApiResult<OrderDetailListVO> result = bizClient.myOrders(userId, pageNum, pageSize, status);
        return result == null ? null : result.getData();
    }

    @Tool(description = "根据商品名称、状态和价格区间获取商品列表")
    public ProductListVO getProductList(Integer pageNum, Integer pageSize, String name, ProductStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        ApiResult<ProductListVO> result = bizClient.productList(pageNum, pageSize, name, status, minPrice, maxPrice);
        return result == null ? null : result.getData();
    }

    @Tool(description = "获取所有商品分类")
    public List<Category> getCategories() {
        ApiResult<List<Category>> result = bizClient.getCategories();
        return result == null ? null : result.getData();
    }
}
