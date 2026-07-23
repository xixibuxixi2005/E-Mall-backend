package com.whut.emall.business;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.Cart;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.mapper.CartMapper;
import com.whut.emall.business.mapper.ProductMapper;
import com.whut.emall.business.service.CartService;
import com.whut.emall.common.vo.CartListVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class CartServiceTest {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private CartService cartService;

    @Resource
    private ProductMapper productMapper;

    private static Integer testCartId;
    private static Integer testProductId;
    private static final Integer TEST_USER_ID = 9999;

    @Test
    @Order(1)
    public void testPrepareProduct() {
        Product p = productMapper.selectOne(new LambdaQueryWrapper<Product>().last("LIMIT 1"));
        if (p != null) {
            testProductId = p.getId();
        }
        Assertions.assertNotNull(testProductId, "没有可测试的商品");
    }

    @Test
    @Order(2)
    public void testSelectCartPage() {
        Page<Cart> page = new Page<>(1, 10);
        Page<Cart> result = cartMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        System.out.println("购物车记录总数：" + result.getTotal());
    }

    @Test
    @Order(3)
    public void testAddToCart() {
        if (testProductId == null) return;
        var detail = cartService.add(TEST_USER_ID, testProductId, 1);
        Assertions.assertNotNull(detail);
        Cart cart = cartMapper.getByUserIdAndProductId(TEST_USER_ID, testProductId);
        Assertions.assertNotNull(cart);
        testCartId = cart.getId();
        System.out.println("添加购物车ID：" + testCartId);
    }

    @Test
    @Order(4)
    public void testListCart() {
        CartListVO vo = cartService.list(TEST_USER_ID);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getItems());
        System.out.println("购物车商品数量：" + vo.getItems().size());
    }

    @Test
    @Order(5)
    public void testListUserCartItems() {
        var items = cartMapper.listUserCartItems(TEST_USER_ID);
        Assertions.assertNotNull(items);
    }

    @Test
    @Order(6)
    public void testGetDetailByUserIdAndProductId() {
        if (testProductId == null) return;
        var detail = cartMapper.getDetailByUserIdAndProductId(TEST_USER_ID, testProductId);
        if (detail != null) {
            Assertions.assertNotNull(detail.getProductName());
        }
    }

    @Test
    @Order(7)
    public void testUpdateCartQuantity() {
        if (testCartId == null) return;
        cartService.update(TEST_USER_ID, testCartId, 3);
        Cart cart = cartMapper.selectById(testCartId);
        Assertions.assertEquals(3, cart.getQuantity());
    }

    @Test
    @Order(8)
    public void testSelectCart() {
        if (testCartId == null) return;
        cartService.select(TEST_USER_ID, testCartId, true);
        Cart cart = cartMapper.selectById(testCartId);
        Assertions.assertTrue(cart.getSelected());
        cartService.select(TEST_USER_ID, testCartId, false);
    }

    @Test
    @Order(9)
    public void testSelectListByIdAndUserId() {
        if (testCartId == null) return;
        cartService.select(TEST_USER_ID, testCartId, true);
        List<Cart> list = cartService.selectListByIdAndUserId(TEST_USER_ID, List.of(testCartId));
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(10)
    public void testRemoveFromCart() {
        if (testCartId == null) return;
        cartService.remove(TEST_USER_ID, List.of(testCartId));
        Cart deleted = cartMapper.selectById(testCartId);
        Assertions.assertNull(deleted);
        System.out.println("删除购物车ID：" + testCartId);
    }

    @Test
    @Order(11)
    public void testClearCart() {
        if (testProductId == null) return;
        cartService.add(TEST_USER_ID, testProductId, 1);
        cartService.clear(TEST_USER_ID);
        var items = cartMapper.listUserCartItems(TEST_USER_ID);
        Assertions.assertTrue(items.isEmpty());
    }
}
