package com.whut.emall.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whut.emall.ai.entity.KnowledgeDoc;
import com.whut.emall.ai.mapper.KnowledgeDocMapper;
import com.whut.emall.ai.utils.FileUtils;
import com.whut.emall.ai.vo.KnowledgeListVO;
import com.whut.emall.ai.vo.KnowledgeVO;
import com.whut.emall.common.entity.ApiException;

import jakarta.annotation.Resource;

@Service
public class KnowledgeService extends ServiceImpl<KnowledgeDocMapper, KnowledgeDoc>{
    @Resource FileUtils fileUtils;

    @Transactional(rollbackFor = Exception.class)
    public KnowledgeVO uploadDoc(MultipartFile file, Integer productId, String category, String title) {
        if (title==null || title.isEmpty())
            title = file.getOriginalFilename();
        String uuid = fileUtils.uploadDoc(file);
        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setTitle(title);
        doc.setCategory(category);
        doc.setProductId(productId);
        doc.setUuid(uuid);
        doc.setFileType(file.getContentType());
        doc.setChunkCount(0);
        save(doc);
        // TODO: 向量化
        return baseMapper.getVo(doc.getId());
    }

    public void deleteDoc(Integer id) {
        KnowledgeDoc doc = getById(id);
        if (doc == null)
            throw ApiException.err(404, "未找到该文档");
        fileUtils.deleteDoc(doc.getUuid());
        removeById(id);
    }

    public KnowledgeListVO listDoc(Integer pageNum, Integer pageSize, String keyword, String category) {
        Page<KnowledgeVO> page = getBaseMapper().getVos(new Page<>(pageNum, pageSize), keyword, category);
        return new KnowledgeListVO(page);
    }
}
