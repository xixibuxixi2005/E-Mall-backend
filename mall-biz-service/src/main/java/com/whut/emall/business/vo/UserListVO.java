package com.whut.emall.business.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserListVO {
    List<UserInfo> list;
    Long total;

    public UserListVO(Page<UserInfo> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
    }
}
