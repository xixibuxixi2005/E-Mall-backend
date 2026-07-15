package com.whut.emall.business.vo;

import java.sql.Timestamp;

import com.whut.emall.business.entity.enums.MessageType;
import com.whut.emall.business.entity.enums.SenderType;

import lombok.Data;

@Data
public class ChatMessageVO {
    Integer id;
    SenderType senderType;
    String senderName;
    String content;
    MessageType msgType;
    String extraData;
    Timestamp createTime;

    String test;
}
