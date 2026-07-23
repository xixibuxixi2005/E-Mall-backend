package com.whut.emall.ai;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.ai.entity.UserRecommend;
import com.whut.emall.ai.mapper.UserRecommendMapper;
import com.whut.emall.ai.service.RecommendService;
import com.whut.emall.ai.vo.RecommendListVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AIApplication.class)
public class RecommendServiceTest {

    @Resource
    private UserRecommendMapper userRecommendMapper;

    @Resource
    private RecommendService recommendService;

    private static Integer testRecommendId;

    @Test
    @Order(1)
    public void testSelectPage() {
        Page<UserRecommend> page = new Page<>(1, 10);
        Page<UserRecommend> result = userRecommendMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        System.out.println("推荐记录总数：" + result.getTotal());
    }

    @Test
    @Order(2)
    public void testInsertRecommend() {
        UserRecommend rec = new UserRecommend();
        rec.setUserId(1);
        rec.setProductId(1);
        rec.setScore(0.85);
        rec.setReason("测试推荐理由");
        userRecommendMapper.insert(rec);
        testRecommendId = rec.getId();
        Assertions.assertNotNull(testRecommendId);
        System.out.println("创建推荐ID：" + testRecommendId);
    }

    @Test
    @Order(3)
    public void testGetVOById() {
        if (testRecommendId == null) return;
        var vo = userRecommendMapper.getVOById(testRecommendId);
        if (vo != null) {
            Assertions.assertNotNull(vo.getProductId());
        }
    }

    @Test
    @Order(4)
    public void testGetVOsByUserId() {
        var list = userRecommendMapper.getVOsByUserId(1);
        Assertions.assertNotNull(list);
        System.out.println("用户1推荐数：" + list.size());
    }

    @Test
    @Order(5)
    public void testGetLastRecommendTime() {
        var time = userRecommendMapper.getLastRecommendTime(1);
        System.out.println("最后推荐时间：" + time);
    }

    @Test
    @Order(6)
    public void testRecommendByBehavior() {
        RecommendListVO vo = recommendService.recommendByBehavior(1, 5);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getRecommendations());
        System.out.println("推荐结果数：" + vo.getRecommendations().size());
    }

    @Test
    @Order(7)
    public void testSelectByUserId() {
        List<UserRecommend> list = userRecommendMapper.selectList(
            new LambdaQueryWrapper<UserRecommend>().eq(UserRecommend::getUserId, 1)
        );
        Assertions.assertNotNull(list);
    }

    @Test
    @Order(8)
    public void testUpdateRecommend() {
        if (testRecommendId == null) return;
        UserRecommend rec = userRecommendMapper.selectById(testRecommendId);
        rec.setScore(0.95);
        rec.setReason("更新后的推荐理由");
        userRecommendMapper.updateById(rec);
        UserRecommend updated = userRecommendMapper.selectById(testRecommendId);
        Assertions.assertEquals(0.95, updated.getScore());
    }

    @Test
    @Order(9)
    public void testDeleteRecommend() {
        if (testRecommendId == null) return;
        userRecommendMapper.deleteById(testRecommendId);
        UserRecommend deleted = userRecommendMapper.selectById(testRecommendId);
        Assertions.assertNull(deleted);
        System.out.println("删除推荐ID：" + testRecommendId);
    }
}
