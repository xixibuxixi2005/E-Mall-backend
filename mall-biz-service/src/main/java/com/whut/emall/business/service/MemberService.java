package com.whut.emall.business.service;

import org.springframework.stereotype.Service;

import com.whut.emall.business.entity.Member;
import com.whut.emall.business.mapper.MemberMapper;

import jakarta.annotation.Resource;

@Service
public class MemberService {
    @Resource MemberMapper memberMapper;
    public Member getMemberById(Long id) {
        return memberMapper.selectById(id);
    }
}
