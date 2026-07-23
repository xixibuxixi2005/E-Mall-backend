package com.whut.emall.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whut.emall.business.entity.Cart;
import com.whut.emall.common.vo.CartDetailVO;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart getByUserIdAndProductId(Integer userId, Integer productId);

    @Select("""
        SELECT c.id as cart_id,
               c.product_id,
               p.name AS product_name,
               p.price AS product_price,
               p.original_price AS original_price,
               JSON_UNQUOTE(JSON_EXTRACT(p.image_urls, '$[0]')) AS product_image,
               c.quantity,
               p.stock,
               c.selected,
               c.create_time AS createTime
        FROM cart c
        LEFT JOIN product p ON c.product_id = p.id
        WHERE c.user_id = #{userId} AND c.product_id = #{productId}
        """)
    CartDetailVO getDetailByUserIdAndProductId(Integer userId, Integer productId);

    @Select("""
        SELECT c.id as cart_id,
               c.product_id,
               p.name AS product_name,
               p.price AS product_price,
               p.original_price AS original_price,
               JSON_UNQUOTE(JSON_EXTRACT(p.image_urls, '$[0]')) AS product_image,
               c.quantity,
               p.stock,
               c.selected,
               c.create_time AS createTime
        FROM cart c
        LEFT JOIN product p ON c.product_id = p.id
        WHERE c.user_id = #{userId}
        ORDER BY c.create_time DESC
        """)
    List<CartDetailVO> listUserCartItems(Integer userId);

    @Select("""
        SELECT c.id as cart_id,
               c.product_id,
               p.name AS product_name,
               p.price AS product_price,
               p.original_price AS original_price,
               JSON_UNQUOTE(JSON_EXTRACT(p.image_urls, '$[0]')) AS product_image,
               c.quantity,
               p.stock,
               c.selected,
               c.create_time AS createTime
        FROM cart c
        LEFT JOIN product p ON c.product_id = p.id
        WHERE c.user_id = #{userId} AND c.id = #{cartId}
        """)
    CartDetailVO getDetailByUserIdAndId(Integer userId, Integer cartId);

    @Update("UPDATE cart SET quantity = #{quantity} WHERE id = #{cartId} AND user_id = #{userId}")
    int updateQuantity(Integer userId, Integer cartId, Integer quantity);

    @Update("UPDATE cart SET selected = #{selected} WHERE id = #{cartId} AND user_id = #{userId}")
    int updateSelected(Integer userId, Integer cartId, Boolean selected);
}
