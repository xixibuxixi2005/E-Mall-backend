package com.whut.emall.business.entity;

import java.sql.Timestamp;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.whut.emall.business.entity.enums.MessageType;
import com.whut.emall.business.entity.enums.SenderType;

import lombok.Data;

@Data
@TableName(value = "chat_message", autoResultMap = true)
public class ChatMessage {
    Integer id;
    Integer sessionId;
    SenderType senderType;
    Integer senderId;
    String content;
    MessageType msgType;
    @TableField(typeHandler = JacksonTypeHandler.class)
    Map<String, Object> extraData;
    Boolean isRead;
    Timestamp readTime;
    Timestamp createTime;
}
