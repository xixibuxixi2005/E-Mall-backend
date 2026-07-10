package com.whut.emall.business.vo;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

@Data
public class SysUserListVO {
    List<SysUserInfo> list;
    Long total;

    public SysUserListVO(Page<SysUserInfo> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
    }
}
