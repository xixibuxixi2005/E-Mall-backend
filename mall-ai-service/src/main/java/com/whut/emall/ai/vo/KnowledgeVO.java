package com.whut.emall.ai.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class KnowledgeVO {
    Integer id;
    String title;
    String category;
    Integer productId;
    String url;
    String fileType;
    Integer status;
    Integer chunkCount;
    Timestamp createTime;
    Timestamp updateTime;
}