package com.whut.emall.business.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.mapper.ProductMapper;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.enums.ProductStatus;
import com.whut.emall.common.utils.OSSFileManager;
import com.whut.emall.common.vo.ProductDetailVO;
import com.whut.emall.common.vo.ProductListVO;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product>{
    @Resource ProductMapper productMapper;
    @Resource OSSFileManager ossFileManager;

    public ProductListVO productlist(Integer pageNum, Integer pageSize, String name, ProductStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        Page<ProductDetailVO> page = new Page<>(pageNum, pageSize);
        Page<ProductDetailVO> result = productMapper.selectProductPage(page, name, status, minPrice, maxPrice);
        return new ProductListVO(result);
    }

    public int createProduct(
        String name,
        String subTitle,
        String description,
        Integer categoryId,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer stock,
        List<String> imageUrls
    ) {
        var product = new Product();
        product.setName(name);
        product.setSubTitle(subTitle);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setStock(stock);
        product.setImageUrls(imageUrls);
        productMapper.insert(product);
        return productMapper.getProductByName(name).getId();
    }

    public ProductDetailVO getProductById(Integer id) {
        ProductDetailVO vo = productMapper.getProductDetailById(id);
        if (vo == null) {
            throw ApiException.err(404, "商品不存在");
        }
        return vo;
    }

    public void updateProduct(Integer id, String name, String subTitle, BigDecimal price, Integer stock, String description, List<String> imageUrls) {
        Product product = productMapper.selectById(id);
        if (product == null)
            throw ApiException.err(404, "商品不存在");
        List<String> imgsNeedToDelete = product.getImageUrls().stream().filter(
            imageUrl -> !imageUrls.contains(imageUrl)
        ).toList();
        product.setId(id);
        product.setName(name);
        product.setSubTitle(subTitle);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description);
        product.setImageUrls(imageUrls);
        productMapper.updateById(product);
        ossFileManager.imagesDelete(imgsNeedToDelete);
    }

    public void deleteProduct(Integer id) {
        Product product = productMapper.selectById(id);
        if (product == null)
            throw ApiException.err(404, "商品不存在");
        ossFileManager.imagesDelete(product.getImageUrls());
        productMapper.deleteById(id);
    }

    public void updateProductStatus(Integer id, ProductStatus status) {
        Product product = productMapper.selectById(id);
        if (product == null)
            throw ApiException.err(404, "商品不存在");
        product.setId(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }
}
