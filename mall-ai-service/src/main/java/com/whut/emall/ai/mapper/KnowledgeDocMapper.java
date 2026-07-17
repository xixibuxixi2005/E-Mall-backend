package com.whut.emall.ai.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.ai.entity.KnowledgeDoc;
import com.whut.emall.ai.vo.KnowledgeVO;

@Mapper
public interface KnowledgeDocMapper extends BaseMapper<KnowledgeDoc>{
    @Select("SELECT * FROM knowledge_doc where id=#{id}")
    public KnowledgeVO getVo(Integer id);

    @Select("""
        <script>
        SELECT * FROM knowledge_doc
        <where>
            <if test="keyword!=null"> AND title LIKE CONCAT('%',#{keyword},'%') </if>
            <if test="category!=null"> AND category=#{category} </if>
        </where>
        ORDER BY create_time DESC
        </script>
    """)
    public Page<KnowledgeVO> getVos(Page<KnowledgeVO> page, String keyword, String category);
}
