package com.whut.emall.ai.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

@Data
public class KnowledgeListVO {
    List<KnowledgeVO> list;
    Long total;
    public KnowledgeListVO(Page<KnowledgeVO> page) {
        total = page.getTotal();
        list = page.getRecords();
    }
}
