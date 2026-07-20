package com.whut.emall.business.mapper;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whut.emall.business.entity.CSStatus;
import com.whut.emall.business.vo.CSStatusVO;

public interface CSStatusMapper extends BaseMapper<CSStatus>{
    @Select("""
        SELECT css.*, su.username as csName
        FROM cs_status css
            LEFT JOIN sys_user su ON css.cs_id = su.id
        WHERE css.cs_id = #{csId}
    """)
    CSStatusVO getVOByCsId(Integer csId);
}
