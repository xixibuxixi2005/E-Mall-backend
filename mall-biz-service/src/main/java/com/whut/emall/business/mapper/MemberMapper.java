package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whut.emall.business.entity.Member;

@Mapper
public interface MemberMapper extends BaseMapper<Member>{ }
