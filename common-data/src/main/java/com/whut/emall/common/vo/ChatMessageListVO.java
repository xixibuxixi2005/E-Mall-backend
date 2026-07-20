package com.whut.emall.common.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageListVO {
    Long total;
    List<ChatMessageVO> list;
    
    public ChatMessageListVO(Page<ChatMessageVO> page) {
        total = page.getTotal();
        list = page.getRecords();
    }
}
