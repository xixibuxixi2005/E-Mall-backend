package com.whut.emall.business.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.whut.emall.business.entity.Cart;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.entity.enums.ProductStatus;
import com.whut.emall.business.mapper.CartMapper;
import com.whut.emall.business.mapper.ProductMapper;
import com.whut.emall.business.vo.CartDetailVO;
import com.whut.emall.business.vo.CartListVO;
import com.whut.emall.common.entity.ApiException;

import jakarta.annotation.Resource;

@Service
public class CartService {
    @Resource CartMapper cartMapper;
    @Resource ProductMapper productMapper;

    public CartDetailVO add(Integer userId, Integer productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != ProductStatus.ON_SALE)
            throw ApiException.err(400, "商品不存在或已下架");
        
        Cart cart = cartMapper.getByUserIdAndProductId(userId, productId);
        if (cart != null) {
            if (cart.getQuantity() + quantity > product.getStock())
                throw ApiException.err(400, "库存不足");
            cartMapper.updateQuantity(userId, cart.getId(), cart.getQuantity() + quantity);
        } else {
            if (quantity > product.getStock())
                throw ApiException.err(400, "库存不足");
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }
        return cartMapper.getDetailByUserIdAndProductId(userId, productId);
    }

    public CartListVO list(Integer userId) {
        List<CartDetailVO> items = cartMapper.listUserCartItems(userId);
        CartListVO vo = new CartListVO();
        vo.setItems(items);
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetailVO item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            totalQuantity = totalQuantity.add(quantity);
            totalPrice = totalPrice.add(item.getProductPrice().multiply(quantity));
        }
        vo.setTotalQuantity(totalQuantity.intValue());
        vo.setTotalPrice(totalPrice);
        return vo;
    }

    public void update(Integer userId, Integer cartId, Integer quantity) {
        CartDetailVO cartItem = cartMapper.getDetailByUserIdAndProductId(userId, cartId);
        if (cartItem == null)
            throw ApiException.err(404, "购物车商品不存在");
        if (quantity > cartItem.getStock())
            throw ApiException.err(400, "库存不足");
        
        cartMapper.updateQuantity(userId, cartId, quantity);
    }

    public void remove(Integer userId, List<Integer> cartIds) {
        cartMapper.deleteByIds(userId, cartIds);
    }

    public void clear(Integer userId) {
        cartMapper.clearByUserId(userId);
    }

    public void select(Integer userId, Integer cartId, Boolean selected) {
        cartMapper.updateSelected(userId, cartId, selected);
    }
}
