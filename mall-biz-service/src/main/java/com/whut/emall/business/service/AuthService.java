package com.whut.emall.business.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whut.emall.business.dto.LoginDTO;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.mapper.MemberMapper;
import com.whut.emall.business.mapper.SysUserMapper;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;
import com.whut.emall.common.utils.PasswordUtils;

import jakarta.annotation.Resource;

@Service
public class AuthService {
    @Resource SysUserMapper sysUserMapper;
    @Resource MemberMapper memberMapper;
    @Resource JwtUtils jwtUtils;

    public Map<String,Object> login(LoginDTO dto) {
        String email = dto.getEmail();
        Map<String,Object> result = new HashMap<>();

        LambdaQueryWrapper<SysUser> wrapperS = new LambdaQueryWrapper<>();
        wrapperS.eq(SysUser::getEmail, email);
        SysUser sysUser = sysUserMapper.selectOne(wrapperS);
        if (sysUser != null) {
            if(!PasswordUtils.verifyPassword(dto.getPassword(), sysUser.getPassword()))
                throw ApiException.err(401, "用户名或密码错误");
            if (sysUser.getStatus() == 0)
                throw ApiException.err(403, "账号已被禁用");
            JwtPayload payload = new JwtPayload(sysUser.getId(), sysUser.getEmail(), sysUser.getRoleCode());
            result.put("token", jwtUtils.makeAccessToken(payload));
            result.put("refreshToken", jwtUtils.makeRefreshToken(payload));
            result.put("username", sysUser.getUsername());
            result.put("roleCode", sysUser.getRoleCode());
            result.put("userId", sysUser.getId());
            result.put("phone", sysUser.getPhone());
        } else {
            LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Member::getEmail, email);
            Member member = memberMapper.selectOne(wrapper);
            if (member == null || !PasswordUtils.verifyPassword(dto.getPassword(), member.getPassword()))
                throw ApiException.err(401, "用户名或密码错误");
            if (member.getStatus() == 0)
                throw ApiException.err(403, "账号已被禁用");
            JwtPayload payload = new JwtPayload(member.getId(), member.getEmail(), "MEMBER");
            result.put("token", jwtUtils.makeAccessToken(payload));
            result.put("refreshToken", jwtUtils.makeRefreshToken(payload));
            result.put("username", member.getUsername());
            result.put("roleCode", "MEMBER");
            result.put("userId", member.getId());
            result.put("phone", member.getPhone());
        }
        return result;
    }
}
