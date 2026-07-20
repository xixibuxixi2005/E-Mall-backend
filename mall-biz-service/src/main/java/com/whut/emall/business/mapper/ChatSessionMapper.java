package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.ChatSession;
import com.whut.emall.business.entity.enums.SessionStatus;
import com.whut.emall.business.vo.ChatSessionVO;

public interface ChatSessionMapper extends BaseMapper<ChatSession>{
    @Select("""
        SELECT cs.*, m.username AS userName, su.username AS csName,
            (SELECT COUNT(*) FROM chat_message cm WHERE cm.session_id=#{id} AND is_read=0) AS unreadCount,
            (SELECT create_time FROM chat_message cm WHERE cm.session_id=#{id} ORDER BY create_time DESC LIMIT 1) AS lastMessageTime,
            CASE
                WHEN cs.source = 'PRODUCT' THEN CONCAT('商品详情-', (SELECT name FROM product WHERE id=cs.source_id))
                ELSE NULL
            END AS sourceName
        FROM chat_session cs
            LEFT JOIN member m ON m.id = cs.user_id
            LEFT JOIN sys_user su ON su.id = cs.cs_id
        WHERE cs.id = #{id}
    """)
    ChatSessionVO getVOById(Integer id);

    @Select("""
        <script>
        SELECT cs.*, m.username AS userName, su.username AS csName,
            (SELECT COUNT(*) FROM chat_message cm WHERE cm.session_id=cs.id AND is_read=0) AS unreadCount,
            (SELECT create_time FROM chat_message cm WHERE cm.session_id=cs.id ORDER BY create_time DESC LIMIT 1) AS lastMessageTime,
            CASE
                WHEN cs.source = 'PRODUCT' THEN CONCAT('商品详情-', (SELECT name FROM product WHERE id=cs.source_id))
                ELSE NULL
            END AS sourceName
        FROM chat_session cs
            LEFT JOIN member m ON m.id = cs.user_id
            LEFT JOIN sys_user su ON su.id = cs.cs_id
        <where>
            <if test="status != null">AND cs.status=#{status}</if>
            <if test="status == null">AND (cs.status=0 or cs.status=1)</if>
            <if test="userId != null">AND cs.user_id=#{userId}</if>
        </where>
        ORDER BY cs.create_time
        </script>
    """)
    Page<ChatSessionVO> getVOs(Page<?> page, Integer userId, SessionStatus status);
}
