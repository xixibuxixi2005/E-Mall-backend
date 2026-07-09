package com.whut.emall.business.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import com.whut.emall.business.mapper.ProductMapper;
import com.whut.emall.business.vo.ProductDetailVO;
import com.whut.emall.common.entity.ApiException;

@Service
public class ProductService {
    @Resource ProductMapper productMapper;

    public ProductDetailVO getProductById(Integer id) {
        ProductDetailVO vo = productMapper.getProductDetailById(id);
        if (vo == null) {
            throw ApiException.err(404, "商品不存在");
        }
        return vo;
    }
}
