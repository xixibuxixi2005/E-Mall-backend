package com.whut.emall.business.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.enums.ProductStatus;
import com.whut.emall.business.mapper.ProductMapper;
import com.whut.emall.business.vo.ProductDetailVO;
import com.whut.emall.business.vo.ProductListVO;
import com.whut.emall.common.entity.ApiException;

@Service
public class ProductService {
    @Resource ProductMapper productMapper;

    public ProductListVO productlist(Integer pageNum, Integer pageSize, String name, ProductStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        Page<ProductDetailVO> page = new Page<>(pageNum, pageSize);
        Page<ProductDetailVO> result = productMapper.selectProductPage(page, name, status, minPrice, maxPrice);
        return new ProductListVO(result);
    }

    public ProductDetailVO getProductById(Integer id) {
        ProductDetailVO vo = productMapper.getProductDetailById(id);
        if (vo == null) {
            throw ApiException.err(404, "商品不存在");
        }
        return vo;
    }
}
