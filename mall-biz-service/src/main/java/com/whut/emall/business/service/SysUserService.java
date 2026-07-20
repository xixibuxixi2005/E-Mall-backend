package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.mapper.SysUserMapper;
import com.whut.emall.business.vo.UserInfo;
import com.whut.emall.business.vo.UserListVO;

import jakarta.annotation.Resource;

@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {
    @Resource SysUserMapper sysUserMapper;

    public UserInfo getInfoByEmail(String email) {
        return sysUserMapper.selectInfoByEmail(email);
    }

    public SysUser getSysUserByEmail(String email) {
        return sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("email", email));
    }
    
    public boolean addSysUser(SysUser sysUser) {
        return sysUserMapper.insert(sysUser) > 0;
    }

    public UserListVO searchUserInfo(Integer pageNum, Integer pageSize, String username, String roleCode) {
        Page<UserInfo> page = new Page<>(pageNum, pageSize);
        var resultPage = sysUserMapper.searchUserInfo(page, username, roleCode);
        return new UserListVO(resultPage);
    }
}
