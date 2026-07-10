package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.vo.SysUserInfo;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.utils.PasswordUtils;

import jakarta.annotation.Resource;

@Service
public class AdminService {
    @Resource SysUserService sysUserService;

    public SysUserInfo userCreate(String email, String password, String username, String phone, String roleCode) {
        if (sysUserService.getInfoByEmail(email)!=null)
            throw ApiException.err(400, "该邮箱已被注册");
        SysUser sysUser = new SysUser();
        sysUser.setEmail(email);
        sysUser.setPassword(PasswordUtils.encryptPassword(password));
        sysUser.setUsername(username);
        sysUser.setPhone(phone);
        sysUser.setRoleCode(roleCode);
        sysUserService.addSysUser(sysUser);
        return sysUserService.getInfoByEmail(email);
    }
}
