package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.vo.ProductDetailVO;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Results({
        @Result(column = "image_urls", property = "imageUrls", typeHandler = JacksonTypeHandler.class)
    })
    @Select("""
        SELECT p.*, c.name AS categoryName
        FROM product p
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.id = #{id}
        """)
    ProductDetailVO getProductDetailById(Integer id);
}
