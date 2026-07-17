package com.whut.emall.ai.client;

import java.math.BigDecimal;
import java.util.List;

import com.whut.emall.common.entity.ApiResult;
import com.whut.emall.common.entity.Category;
import com.whut.emall.common.entity.enums.OrderStatus;
import com.whut.emall.common.entity.enums.ProductStatus;
import com.whut.emall.common.vo.CartListVO;
import com.whut.emall.common.vo.OrderDetailListVO;
import com.whut.emall.common.vo.ProductDetailVO;
import com.whut.emall.common.vo.ProductListVO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "mall-biz-service", path = "/api")
public interface BizClient {
    @GetMapping("/chat/cs/messages")
    Object getHistories(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Integer sessionId);
    
    @GetMapping("/biz/cart/list")
    ApiResult<CartListVO> cartList(@RequestHeader("X-User-Id") Integer userId);

    @GetMapping("/biz/order/my/list")
    ApiResult<OrderDetailListVO> myOrders(
        @RequestHeader("X-User-Id") Integer userId,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) OrderStatus status
    );

    @GetMapping("/biz/product/{id}")
    ApiResult<ProductDetailVO> getProductDetail(@PathVariable("id") Integer id);

    @GetMapping("/biz/product/list")
    ApiResult<ProductListVO> productList(
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) ProductStatus status,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice
    );

    @GetMapping("/biz/category")
    ApiResult<List<Category>> getCategories();
}
