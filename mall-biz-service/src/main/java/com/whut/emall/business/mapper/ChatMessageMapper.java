package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.ChatMessage;
import com.whut.emall.business.vo.ChatMessageVO;

public interface ChatMessageMapper extends BaseMapper<ChatMessage>{
    @Select("""
        SELECT cm.*,
            CASE
                WHEN sender_type = 0 THEN (SELECT username FROM member WHERE id=cm.sender_id)
                WHEN sender_type = 1 THEN (SELECT username FROM sys_user WHERE id=cm.sender_id)
                ELSE 'AI自动回复'
            END AS senderName
        FROM chat_message cm
        WHERE cm.session_id = #{sessionId}
    """)
    Page<ChatMessageVO> getVOsBySessionId(Page<?> page, Integer sessionId);

    @Select("""
        SELECT cm.*,
            CASE
                WHEN sender_type = 0 THEN (SELECT username FROM member WHERE id=cm.sender_id)
                WHEN sender_type = 1 THEN (SELECT username FROM sys_user WHERE id=cm.sender_id)
                ELSE 'AI自动回复'
            END AS senderName
        FROM chat_message cm
        WHERE cm.id = #{id}
    """)
    ChatMessageVO getVOById(Integer id);

    @Update("UPDATE chat_message SET is_read=1 WHERE session_id = #{sessionId}")
    int readAllBySessionId(Integer sessionId);
}
