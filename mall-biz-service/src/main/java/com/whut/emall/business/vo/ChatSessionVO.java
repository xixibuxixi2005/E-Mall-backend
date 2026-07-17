package com.whut.emall.business.vo;

import java.sql.Timestamp;

import com.whut.emall.business.entity.enums.SessionMode;
import com.whut.emall.business.entity.enums.SessionStatus;

import lombok.Data;

@Data
public class ChatSessionVO {
    Integer id;
    String sessionNo;
    Integer userId;
    String userName;
    Integer csId;
    String csName;
    SessionStatus status;
    SessionMode mode;
    String firstMessage;
    Timestamp lastMessageTime;
    Integer unreadCount;
    Timestamp startTime;
    Timestamp endTime;
    String source;
    String sourceId;
    String sourceName;
}
