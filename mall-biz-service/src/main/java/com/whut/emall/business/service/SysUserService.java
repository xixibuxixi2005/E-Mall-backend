package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.mapper.SysUserMapper;
import com.whut.emall.business.vo.SysUserInfo;

import jakarta.annotation.Resource;

@Service
public class SysUserService {
    @Resource SysUserMapper sysUserMapper;

    public SysUserInfo getInfoByEmail(String email) {
        return sysUserMapper.selectInfoByEmail(email);
    }

    public SysUser getSysUserByEmail(String email) {
        return sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("email", email));
    }
    
    public boolean addSysUser(SysUser sysUser) {
        return sysUserMapper.insert(sysUser) > 0;
    }
}
