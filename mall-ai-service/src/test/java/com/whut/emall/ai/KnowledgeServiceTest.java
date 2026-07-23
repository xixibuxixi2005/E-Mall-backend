package com.whut.emall.ai;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.ai.entity.KnowledgeDoc;
import com.whut.emall.ai.mapper.KnowledgeDocMapper;
import com.whut.emall.ai.service.KnowledgeService;
import com.whut.emall.ai.vo.KnowledgeListVO;
import com.whut.emall.ai.vo.KnowledgeVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AIApplication.class)
public class KnowledgeServiceTest {

    @Resource
    private KnowledgeDocMapper knowledgeDocMapper;

    @Resource
    private KnowledgeService knowledgeService;

    private static Integer testDocId;

    @Test
    @Order(1)
    public void testListKnowledge() {
        List<KnowledgeDoc> list = knowledgeDocMapper.selectList(null);
        System.out.println("文档总数：" + list.size());
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(2)
    public void testListDoc() {
        KnowledgeListVO vo = knowledgeService.listDoc(1, 10, null, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
        System.out.println("知识文档总数：" + vo.getTotal());
    }

    @Test
    @Order(3)
    public void testListDocWithKeyword() {
        KnowledgeListVO vo = knowledgeService.listDoc(1, 10, "产品", null);
        Assertions.assertNotNull(vo);
    }

    @Test
    @Order(4)
    public void testSelectPage() {
        Page<KnowledgeDoc> page = new Page<>(1, 10);
        Page<KnowledgeDoc> result = knowledgeDocMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
    }

    @Test
    @Order(5)
    public void testCreateKnowledgeDoc() {
        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setTitle("测试文档_JUnit");
        doc.setCategory("测试分类");
        doc.setProductId(1);
        doc.setUrl("http://example.com/test.pdf");
        doc.setFileType("pdf");
        doc.setStatus(1);
        doc.setChunkCount(0);
        knowledgeDocMapper.insert(doc);
        testDocId = doc.getId();
        Assertions.assertNotNull(testDocId);
        System.out.println("创建文档ID：" + testDocId);
    }

    @Test
    @Order(6)
    public void testGetVo() {
        if (testDocId == null) return;
        KnowledgeVO vo = knowledgeDocMapper.getVo(testDocId);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getTitle());
    }

    @Test
    @Order(7)
    public void testGetVos() {
        Page<KnowledgeVO> page = knowledgeDocMapper.getVos(new Page<>(1, 10), null, null);
        Assertions.assertNotNull(page);
        Assertions.assertNotNull(page.getRecords());
    }

    @Test
    @Order(8)
    public void testSelectById() {
        if (testDocId == null) return;
        KnowledgeDoc doc = knowledgeDocMapper.selectById(testDocId);
        Assertions.assertNotNull(doc);
        Assertions.assertEquals("测试文档_JUnit", doc.getTitle());
    }

    @Test
    @Order(9)
    public void testUpdateKnowledgeDoc() {
        if (testDocId == null) return;
        KnowledgeDoc doc = knowledgeDocMapper.selectById(testDocId);
        doc.setTitle("测试文档_JUnit_Updated");
        doc.setCategory("更新后的分类");
        knowledgeDocMapper.updateById(doc);
        KnowledgeDoc updated = knowledgeDocMapper.selectById(testDocId);
        Assertions.assertEquals("测试文档_JUnit_Updated", updated.getTitle());
        Assertions.assertEquals("更新后的分类", updated.getCategory());
    }

    @Test
    @Order(10)
    public void testDeleteKnowledgeDoc() {
        if (testDocId == null) return;
        knowledgeDocMapper.deleteById(testDocId);
        KnowledgeDoc deleted = knowledgeDocMapper.selectById(testDocId);
        Assertions.assertNull(deleted);
        System.out.println("删除文档ID：" + testDocId);
    }
}
