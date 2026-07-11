package com.whut.emall.business.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.entity.enums.ProductStatus;
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

    @Results({
        @Result(column = "image_urls", property = "imageUrls", typeHandler = JacksonTypeHandler.class)
    })
    @Select("""
        <script>
        SELECT p.*, c.name AS categoryName
        FROM product p
        LEFT JOIN category c ON p.category_id = c.id
        <where>
            <if test="name != null and name != ''">
                AND (
                    p.name LIKE CONCAT('%', #{name}, '%')
                    OR p.sub_title LIKE CONCAT('%', #{name}, '%')
                    OR c.name LIKE CONCAT('%', #{name}, '%')
                )
            </if>
            <if test="status != null">
                AND p.status = #{status}
            </if>
            <if test="minPrice != null">
                AND p.price &gt;= #{minPrice}
            </if>
            <if test="maxPrice != null">
                AND p.price &lt;= #{maxPrice}
            </if>
        </where>
        ORDER BY p.create_time DESC
        </script>
        """)
    Page<ProductDetailVO> selectProductPage(Page<ProductDetailVO> page,
        String name,
        ProductStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice
    );
}
