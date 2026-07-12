package com.whut.emall.business.mapper;

import java.sql.Timestamp;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.Order;
import com.whut.emall.business.entity.enums.OrderStatus;
import com.whut.emall.business.vo.OrderVO;

public interface OrderMapper extends BaseMapper<Order>{
    @Select("""
        <script>
        SELECT o.*, m.username FROM `order` o
            LEFT JOIN member m ON o.user_id = m.id
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
        ORDER BY o.create_time DESC
        </script>
    """)
    Page<OrderVO> orderList(Page<OrderVO> page, String orderNo, Integer userId, OrderStatus status, Timestamp startTime, Timestamp endTime);
}
