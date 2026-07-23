package com.whut.emall.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.CSStatus;
import com.whut.emall.business.entity.enums.CSStatusStatus;
import com.whut.emall.business.mapper.CSStatusMapper;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class CSStatusServiceTest {

    @Resource
    private CSStatusMapper csStatusMapper;

    private static Integer testCsStatusId;
    private static final Integer TEST_CS_ID = 6;

    @Test
    @Order(1)
    public void testSelectPage() {
        Page<CSStatus> page = new Page<>(1, 10);
        Page<CSStatus> result = csStatusMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        System.out.println("客服状态总数：" + result.getTotal());
    }

    @Test
    @Order(3)
    public void testGetVOByCsId() {
        if (testCsStatusId == null) return;
        var vo = csStatusMapper.getVOByCsId(TEST_CS_ID);
        if (vo != null) {
            Assertions.assertNotNull(vo.getStatus());
        }
    }

    @Test
    @Order(4)
    public void testSelectById() {
        if (testCsStatusId == null) return;
        CSStatus status = csStatusMapper.selectById(testCsStatusId);
        Assertions.assertNotNull(status);
        Assertions.assertEquals(CSStatusStatus.ONLINE, status.getStatus());
    }

    @Test
    @Order(5)
    public void testUpdateStatus() {
        if (testCsStatusId == null) return;
        CSStatus status = csStatusMapper.selectById(testCsStatusId);
        status.setStatus(CSStatusStatus.BUSY);
        status.setCurrentCount(2);
        csStatusMapper.updateById(status);
        CSStatus updated = csStatusMapper.selectById(testCsStatusId);
        Assertions.assertEquals(CSStatusStatus.BUSY, updated.getStatus());
        Assertions.assertEquals(2, updated.getCurrentCount());
    }

    @Test
    @Order(6)
    public void testUpdateToOffline() {
        if (testCsStatusId == null) return;
        CSStatus status = csStatusMapper.selectById(testCsStatusId);
        status.setStatus(CSStatusStatus.OFFLINE);
        status.setCurrentCount(0);
        csStatusMapper.updateById(status);
        CSStatus updated = csStatusMapper.selectById(testCsStatusId);
        Assertions.assertEquals(CSStatusStatus.OFFLINE, updated.getStatus());
    }

    @Test
    @Order(7)
    public void testDeleteCSStatus() {
        if (testCsStatusId == null) return;
        csStatusMapper.deleteById(testCsStatusId);
        CSStatus deleted = csStatusMapper.selectById(testCsStatusId);
        Assertions.assertNull(deleted);
        System.out.println("删除客服状态ID：" + testCsStatusId);
    }
}
