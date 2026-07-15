package com.whut.emall.business.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

@Data
public class ChatSessionListVO {
    Long total;
    List<ChatSessionVO> list;
    
    public ChatSessionListVO(Page<ChatSessionVO> page) {
        total = page.getTotal();
        list = page.getRecords();
    }
}
