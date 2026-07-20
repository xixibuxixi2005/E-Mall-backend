package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whut.emall.business.entity.Member;
import com.whut.emall.common.vo.MemberInfo;

@Mapper
public interface MemberMapper extends BaseMapper<Member>{
    @Select("SELECT * FROM member WHERE id=#{id}")
    public MemberInfo selectInfoById(Integer id);
}
