package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.mapper.MemberMapper;
import com.whut.emall.business.vo.MemberInfo;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.utils.PasswordUtils;

import jakarta.annotation.Resource;

@Service
public class MemberService {
    @Resource MemberMapper memberMapper;

    public Member getMemberById(Integer id) {
        return memberMapper.selectById(id);
    }

    public MemberInfo getInfoById(Integer id) {
        return memberMapper.selectInfoById(id);
    }
    
    public Member getMemberByEmail(String email) {
        return memberMapper.selectOne(new QueryWrapper<Member>().eq("email", email));
    }

    public boolean addMember(Member member) {
        return memberMapper.insert(member) > 0;
    }

    public void setInfoById(int userId, String email, String phone, String avatar) {
        if (getMemberById(userId) == null)
            throw ApiException.err(404, "该用户不存在");
        Member member = getMemberByEmail(email);
        if (member != null && member.getId() != userId)
            throw ApiException.err(400, "该邮箱已被占用");
            
        member = new Member();
        member.setId(userId);
        member.setEmail(email);
        member.setPhone(phone);
        // TODO: member.setAvatar(avatar);
        memberMapper.updateById(member);
    }
    
    public void setPassword(int userId, String oldPassword, String newPassword) {
        Member member = getMemberById(userId);
        if (member == null)
            throw ApiException.err(404, "该用户不存在");
        if(!PasswordUtils.verifyPassword(oldPassword, member.getPassword()))
            throw ApiException.err(403, "旧密码错误");
        member.setPassword(PasswordUtils.encryptPassword(newPassword));
        memberMapper.updateById(member);
    }
}
