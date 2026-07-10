package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.vo.SysUserInfo;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser>{
    @Select("SELECT * FROM sys_user WHERE email=#{email}")
    public SysUserInfo selectInfoByEmail(String email);
}
