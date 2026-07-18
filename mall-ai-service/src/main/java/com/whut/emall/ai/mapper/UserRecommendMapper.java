package com.whut.emall.ai.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whut.emall.ai.entity.UserRecommend;
import com.whut.emall.ai.vo.RecommendVO;

public interface UserRecommendMapper extends BaseMapper<UserRecommend>{
    @Select("""
        SELECT ur.*, p.name, p.price,
        JSON_UNQUOTE(JSON_EXTRACT(p.image_urls, '$[0]')) AS imageUrl
        FROM user_recommend ur
            LEFT JOIN product p ON ur.product_id = p.id
        WHERE ur.id = #{id}
    """)
    RecommendVO getVOById(Integer id);

    @Select("""
        SELECT ur.*, p.name, p.price,
        JSON_UNQUOTE(JSON_EXTRACT(p.image_urls, '$[0]')) AS imageUrl
        FROM user_recommend ur
            LEFT JOIN product p ON ur.product_id = p.id
        WHERE ur.user_id = #{userId}
    """)
    List<RecommendVO> getVOsByUserId(Integer userId);

    @Select("SELECT MAX(create_time) FROM user_recommend WHERE user_id = #{userId}")
    Timestamp getLastRecommendTime(Integer userId);
}