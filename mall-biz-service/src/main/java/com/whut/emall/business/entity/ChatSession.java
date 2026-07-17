package com.whut.emall.business.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.whut.emall.business.entity.enums.SessionMode;
import com.whut.emall.business.entity.enums.SessionStatus;

import lombok.Data;

@Data
@TableName("chat_session")
public class ChatSession {
    Integer id;
    String sessionNo;
    Integer userId;
    Integer csId;
    SessionStatus status;
    SessionMode mode;
    Timestamp startTime;
    Timestamp endTime;
    String firstMessage;
    String source;
    String sourceId;
    LocalDateTime updateTime;
}
