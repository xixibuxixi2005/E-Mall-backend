package com.whut.emall.business.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.Order;
import com.whut.emall.business.entity.enums.OrderStatus;
import com.whut.emall.business.vo.OrderItemVO;
import com.whut.emall.business.vo.OrderVO;

public interface OrderMapper extends BaseMapper<Order>{
    @Select("""
        SELECT o.*, m.username, COALESCE(SUM(oi.quantity * p.price), 0) AS totalAmount
            FROM `order` o
            LEFT JOIN member m ON o.user_id = m.id
            LEFT JOIN order_item oi ON o.id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.id
        WHERE o.id = #{id}
    """)
    OrderVO selectOrderVOById(Integer id);

    @Select("""
        SELECT oi.*, p.name AS productName, p.price AS productPrice,
            oi.quantity * p.price AS totalPrice
            FROM order_item oi
            LEFT JOIN product p ON oi.product_id = p.id
        WHERE oi.order_id = #{orderId}
    """)
    List<OrderItemVO> selectItemsByOrderId(Integer orderId);

    @Select("""
        <script>
        SELECT o.*, m.username, COALESCE(SUM(oi.quantity * p.price), 0) AS totalAmount
            FROM `order` o
            LEFT JOIN member m ON o.user_id = m.id
            LEFT JOIN order_item oi ON o.id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.id
            <where>
                <if test="orderNo != null and orderNo != ''">
                    AND o.order_no = #{orderNo}
                </if>
                <if test="userId != null">
                    AND o.user_id = #{userId}
                </if>
                <if test="status != null">
                    AND o.status = #{status}
                </if>
                <if test="startTime != null">
                    AND o.create_time &gt;= #{startTime}
                </if>
                <if test="endTime != null">
                    AND o.create_time &lt;= #{endTime}
                </if>
            </where>
        GROUP BY o.id
        ORDER BY o.create_time DESC
        </script>
    """)
    Page<OrderVO> orderList(Page<OrderVO> page, String orderNo, Integer userId, OrderStatus status, Timestamp startTime, Timestamp endTime);
}
