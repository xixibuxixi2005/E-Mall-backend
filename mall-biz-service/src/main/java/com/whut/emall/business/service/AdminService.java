package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.business.vo.SysUserInfo;
import com.whut.emall.business.vo.SysUserListVO;
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

    public SysUserListVO userList(Integer pageNum, Integer pageSize, String username, String roleCode) {
        Page<SysUser> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class)
            .like(username != null && !username.isBlank(), SysUser::getUsername, username)
            .eq(roleCode != null && !roleCode.isBlank(), SysUser::getRoleCode, roleCode)
            .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserService.page(page, wrapper);
        Page<SysUserInfo> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(user -> {
            SysUserInfo info = new SysUserInfo();
            info.setId(user.getId());
            info.setUsername(user.getUsername());
            info.setPhone(user.getPhone());
            info.setRoleCode(user.getRoleCode());
            info.setStatus(user.getStatus() == null ? UserStatus.VALID : user.getStatus());
            info.setCreateTime(user.getCreateTime());
            return info;
        }).toList());
        return new SysUserListVO(voPage);
    }
}
