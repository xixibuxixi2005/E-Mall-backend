package com.whut.emall.business;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.mapper.ProductMapper;
import com.whut.emall.business.service.ProductService;
import com.whut.emall.common.entity.enums.ProductStatus;
import com.whut.emall.common.vo.ProductDetailVO;
import com.whut.emall.common.vo.ProductListVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class ProductServiceTest {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductService productService;

    private static Integer testProductId;

    @Test
    @Order(1)
    public void testProductPage() {
        Page<Product> page = new Page<>(1, 10);
        Page<Product> result = productMapper.selectPage(page, null);
        System.out.println("商品总数：" + result.getTotal());
        Assertions.assertNotNull(result.getRecords());
        Assertions.assertTrue(result.getTotal() >= 0);
    }

    @Test
    @Order(2)
    public void testProductListVO() {
        ProductListVO vo = productService.productlist(1, 10, null, null, null, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
        System.out.println("商品列表总数：" + vo.getTotal());
    }

    @Test
    @Order(3)
    public void testProductListWithFilter() {
        ProductListVO vo = productService.productlist(1, 10, null, ProductStatus.ON_SALE, null, null);
        Assertions.assertNotNull(vo);
        System.out.println("在售商品数：" + vo.getTotal());
    }

    @Test
    @Order(4)
    public void testCreateProduct() {
        List<String> imageUrls = List.of("test1.jpg", "test2.jpg");
        int productId = productService.createProduct(
            "测试商品_JUnit",
            "测试副标题",
            "测试描述",
            1,
            new BigDecimal("99.99"),
            new BigDecimal("199.99"),
            100,
            imageUrls
        );
        testProductId = productId;
        Assertions.assertTrue(productId > 0);
        System.out.println("创建商品ID：" + productId);
    }

    @Test
    @Order(5)
    public void testGetProductById() {
        if (testProductId == null) {
            Product p = productMapper.selectOne(new LambdaQueryWrapper<Product>().last("LIMIT 1"));
            if (p != null) testProductId = p.getId();
        }
        Assertions.assertNotNull(testProductId, "没有可测试的商品");
        ProductDetailVO vo = productService.getProductById(testProductId);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getName());
        System.out.println("商品详情：" + vo.getName());
    }

    @Test
    @Order(6)
    public void testUpdateProduct() {
        if (testProductId == null) return;
        List<String> imageUrls = List.of("test1.jpg", "test3.jpg");
        productService.updateProduct(
            testProductId,
            "测试商品_JUnit_Updated",
            "更新后的副标题",
            new BigDecimal("88.88"),
            99,
            "更新后的描述",
            imageUrls
        );
        ProductDetailVO vo = productService.getProductById(testProductId);
        Assertions.assertEquals("测试商品_JUnit_Updated", vo.getName());
        Assertions.assertEquals(0, new BigDecimal("88.88").compareTo(vo.getPrice()));
    }

    @Test
    @Order(7)
    public void testUpdateProductStatus() {
        if (testProductId == null) return;
        productService.updateProductStatus(testProductId, ProductStatus.OFF_SALE);
        Product product = productMapper.selectById(testProductId);
        Assertions.assertEquals(ProductStatus.OFF_SALE, product.getStatus());
        productService.updateProductStatus(testProductId, ProductStatus.ON_SALE);
    }

    @Test
    @Order(8)
    public void testSelectProductPage() {
        Page<ProductDetailVO> page = new Page<>(1, 10);
        Page<ProductDetailVO> result = productMapper.selectProductPage(page, null, null, null, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
    }

    @Test
    @Order(9)
    public void testGetProductByName() {
        Product product = productMapper.getProductByName("测试商品_JUnit_Updated");
        if (product != null) {
            Assertions.assertNotNull(product.getName());
        }
    }

    @Test
    @Order(10)
    public void testDeleteProduct() {
        if (testProductId == null) return;
        Product product = productMapper.selectById(testProductId);
        if (product != null) {
            product.setImageUrls(List.of());
            productMapper.updateById(product);
        }
        productMapper.deleteById(testProductId);
        Product deleted = productMapper.selectById(testProductId);
        Assertions.assertNull(deleted);
        System.out.println("删除商品ID：" + testProductId);
    }
}
