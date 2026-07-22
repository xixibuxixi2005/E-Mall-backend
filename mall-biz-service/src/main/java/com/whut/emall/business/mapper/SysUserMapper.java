package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.vo.UserInfo;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser>{
    @Select("SELECT * FROM sys_user WHERE email=#{email}")
    public UserInfo selectInfoByEmail(String email);

    @Select("""
        <script>
        SELECT id, username, phone, role_code, status, create_time FROM sys_user
        UNION ALL
        SELECT id, username, phone, 'MEMBER' AS role_code, status, create_time FROM member
        <where>
            <if test="username != null and username != ''">
                AND username LIKE CONCAT('%', #{username}, '%')
            </if>
            <if test="roleCode != null and roleCode != ''">
                AND role_code = #{roleCode}
            </if>
        </where>
        ORDER BY create_time DESC
        </script>
    """)
    public Page<UserInfo> searchUserInfo(Page<?> page, String username, String roleCode);
}
