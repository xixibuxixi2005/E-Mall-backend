package com.whut.emall.ai.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("knowledge_doc")
public class KnowledgeDoc {
    Integer id;
    String title;
    String category;
    Integer productId;
    String fileName;
    String fileType;
    Integer status;
    Integer chunkCount;
    Timestamp createTime;
    Timestamp updateTime;
}
