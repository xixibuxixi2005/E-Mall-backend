package com.whut.emall.business.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;
import com.whut.emall.business.entity.enums.CSStatusStatus;

import lombok.Data;

@Data
@TableName("cs_status")
public class CSStatus {
    Integer id;
    Integer csId;
    CSStatusStatus status;
    Integer maxConcurrent;
    Integer currentCount;
    Timestamp lastActiveTime;
    Timestamp updateTime;
}
