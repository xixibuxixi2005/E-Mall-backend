package com.whut.emall.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.business.mapper.SysUserMapper;
import com.whut.emall.business.service.AdminService;
import com.whut.emall.business.service.SysUserService;
import com.whut.emall.business.vo.UserInfo;
import com.whut.emall.business.vo.UserListVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class AdminServiceTest {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private AdminService adminService;

    private static Integer testUserId;
    private static final String TEST_EMAIL = "admin_junit@example.com";

    @Test
    @Order(1)
    public void testSelectSysUserPage() {
        Page<SysUser> page = new Page<>(1, 10);
        Page<SysUser> result = sysUserMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        System.out.println("系统用户总数：" + result.getTotal());
    }

    @Test
    @Order(2)
    public void testUserCreate() {
        UserInfo user = adminService.userCreate(
            TEST_EMAIL,
            "Test@123456",
            "测试管理员",
            "13800138000",
            "ADMIN"
        );
        Assertions.assertNotNull(user);
        Assertions.assertEquals(TEST_EMAIL, user.getEmail());
        testUserId = user.getId();
        System.out.println("创建管理员ID：" + testUserId);
    }

    @Test
    @Order(3)
    public void testGetSysUserByEmail() {
        SysUser user = sysUserService.getSysUserByEmail(TEST_EMAIL);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    @Order(4)
    public void testGetInfoByEmail() {
        UserInfo info = sysUserService.getInfoByEmail(TEST_EMAIL);
        Assertions.assertNotNull(info);
        Assertions.assertNotNull(info.getUsername());
    }

    @Test
    @Order(5)
    public void testUserList() {
        UserListVO vo = adminService.userList(1, 10, null, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
        System.out.println("用户列表总数：" + vo.getTotal());
    }

    @Test
    @Order(6)
    public void testUserListByRole() {
        UserListVO vo = adminService.userList(1, 10, null, "ADMIN");
        Assertions.assertNotNull(vo);
    }

    @Test
    @Order(7)
    public void testSearchUserInfo() {
        Page<UserInfo> page = sysUserMapper.searchUserInfo(new Page<>(1, 10), "测试", null);
        Assertions.assertNotNull(page);
    }

    @Test
    @Order(8)
    public void testSetUserStatus() {
        if (testUserId == null) return;
        adminService.setUserStatus(testUserId, UserStatus.INVALID);
        SysUser user = sysUserMapper.selectById(testUserId);
        Assertions.assertEquals(UserStatus.INVALID, user.getStatus());

        adminService.setUserStatus(testUserId, UserStatus.NORMAL);
        SysUser user2 = sysUserMapper.selectById(testUserId);
        Assertions.assertEquals(UserStatus.NORMAL, user2.getStatus());
    }

    @Test
    @Order(9)
    public void testUpdateSysUser() {
        if (testUserId == null) return;
        SysUser user = sysUserMapper.selectById(testUserId);
        user.setUsername("测试管理员_Updated");
        sysUserMapper.updateById(user);
        SysUser updated = sysUserMapper.selectById(testUserId);
        Assertions.assertEquals("测试管理员_Updated", updated.getUsername());
    }

    @Test
    @Order(10)
    public void testDeleteSysUser() {
        if (testUserId == null) return;
        sysUserMapper.deleteById(testUserId);
        SysUser deleted = sysUserMapper.selectById(testUserId);
        Assertions.assertNull(deleted);
        System.out.println("删除管理员ID：" + testUserId);
    }
}
