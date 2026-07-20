package com.whut.emall.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;
import lombok.Data;

@Data
@TableName("user_recommend")
public class UserRecommend {
    Integer id;
    Integer userId;
    Integer productId;
    Double score;
    String reason;
    Timestamp createTime;
}