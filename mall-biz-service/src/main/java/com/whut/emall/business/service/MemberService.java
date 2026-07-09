package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.mapper.MemberMapper;
import com.whut.emall.business.vo.MemberInfo;

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
}
