package com.whut.emall.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.business.mapper.MemberMapper;
import com.whut.emall.business.service.MemberService;
import com.whut.emall.common.entity.enums.MemberLevel;
import com.whut.emall.common.vo.MemberInfo;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class MemberServiceTest {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MemberService memberService;

    private static Integer testMemberId;
    private static final String TEST_EMAIL = "test_junit@example.com";

    @Test
    @Order(1)
    public void testGetMemberById() {
        Member member = memberMapper.selectById(1);
        if (member != null) {
            System.out.println("会员信息：" + member.getEmail());
            Assertions.assertNotNull(member);
            Assertions.assertNotNull(member.getEmail());
        }
    }

    @Test
    @Order(2)
    public void testAddMember() {
        Member member = new Member();
        member.setEmail(TEST_EMAIL);
        member.setUsername("测试会员JUnit");
        member.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        member.setPhone("13800138000");
        member.setLevel(MemberLevel.NORMAL);
        member.setStatus(UserStatus.VALID);
        boolean result = memberService.addMember(member);
        Assertions.assertTrue(result);
        Member saved = memberService.getMemberByEmail(TEST_EMAIL);
        Assertions.assertNotNull(saved);
        testMemberId = saved.getId();
        System.out.println("创建会员ID：" + testMemberId);
    }

    @Test
    @Order(3)
    public void testGetMemberByEmail() {
        Member member = memberService.getMemberByEmail(TEST_EMAIL);
        Assertions.assertNotNull(member);
        Assertions.assertEquals(TEST_EMAIL, member.getEmail());
    }

    @Test
    @Order(4)
    public void testGetInfoById() {
        if (testMemberId == null) return;
        MemberInfo info = memberService.getInfoById(testMemberId);
        Assertions.assertNotNull(info);
    }

    @Test
    @Order(5)
    public void testSelectMemberPage() {
        Page<Member> page = new Page<>(1, 10);
        Page<Member> result = memberMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        System.out.println("会员总数：" + result.getTotal());
    }

    @Test
    @Order(6)
    public void testUpdateMemberInfo() {
        if (testMemberId == null) return;
        Member member = memberMapper.selectById(testMemberId);
        member.setUsername("测试会员_Updated");
        memberMapper.updateById(member);
        Member updated = memberMapper.selectById(testMemberId);
        Assertions.assertEquals("测试会员_Updated", updated.getUsername());
    }

    @Test
    @Order(7)
    public void testSetInfoById() {
        if (testMemberId == null) return;
        memberService.setInfoById(testMemberId, TEST_EMAIL, "13900139000", "");
        Member updated = memberMapper.selectById(testMemberId);
        Assertions.assertEquals("13900139000", updated.getPhone());
    }

    @Test
    @Order(8)
    public void testSelectByCondition() {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getStatus, UserStatus.VALID);
        var list = memberMapper.selectList(wrapper);
        Assertions.assertNotNull(list);
        System.out.println("正常状态会员数：" + list.size());
    }

    @Test
    @Order(9)
    public void testDeleteMember() {
        if (testMemberId == null) return;
        memberMapper.deleteById(testMemberId);
        Member deleted = memberMapper.selectById(testMemberId);
        Assertions.assertNull(deleted);
        System.out.println("删除会员ID：" + testMemberId);
    }
}
