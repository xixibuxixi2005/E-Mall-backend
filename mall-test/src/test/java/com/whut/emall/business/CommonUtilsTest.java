package com.whut.emall.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.whut.emall.common.entity.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;
import com.whut.emall.common.utils.PasswordUtils;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class CommonUtilsTest {

    @Resource
    private JwtUtils jwtUtils;

    private static String testAccessToken;
    private static String testRefreshToken;
    private static final JwtPayload TEST_PAYLOAD = new JwtPayload(1, "test@example.com", "MEMBER");

    @Test
    @Order(1)
    public void testPasswordEncrypt() {
        String rawPassword = "Test@123456";
        String encrypted = PasswordUtils.encryptPassword(rawPassword);
        Assertions.assertNotNull(encrypted);
        Assertions.assertNotEquals(rawPassword, encrypted);
        System.out.println("加密后密码：" + encrypted);
    }

    @Test
    @Order(2)
    public void testPasswordVerifySuccess() {
        String rawPassword = "Test@123456";
        String encrypted = PasswordUtils.encryptPassword(rawPassword);
        boolean result = PasswordUtils.verifyPassword(rawPassword, encrypted);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(3)
    public void testPasswordVerifyFail() {
        String rawPassword = "Test@123456";
        String encrypted = PasswordUtils.encryptPassword(rawPassword);
        boolean result = PasswordUtils.verifyPassword("WrongPassword", encrypted);
        Assertions.assertFalse(result);
    }

    @Test
    @Order(4)
    public void testMakeAccessToken() {
        String token = jwtUtils.makeAccessToken(TEST_PAYLOAD);
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
        testAccessToken = token;
        System.out.println("Access Token：" + token);
    }

    @Test
    @Order(5)
    public void testMakeRefreshToken() {
        String token = jwtUtils.makeRefreshToken(TEST_PAYLOAD);
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
        testRefreshToken = token;
        System.out.println("Refresh Token：" + token);
    }

    @Test
    @Order(6)
    public void testParserAccessToken() {
        if (testAccessToken == null) return;
        JwtPayload payload = jwtUtils.parserAccessToken(testAccessToken);
        Assertions.assertNotNull(payload);
        Assertions.assertEquals(TEST_PAYLOAD.getUserId(), payload.getUserId());
        Assertions.assertEquals(TEST_PAYLOAD.getEmail(), payload.getEmail());
        Assertions.assertEquals(TEST_PAYLOAD.getRole(), payload.getRole());
    }

    @Test
    @Order(7)
    public void testParserRefreshToken() {
        if (testRefreshToken == null) return;
        JwtPayload payload = jwtUtils.parserRefreshToken(testRefreshToken);
        Assertions.assertNotNull(payload);
        Assertions.assertEquals(TEST_PAYLOAD.getUserId(), payload.getUserId());
    }

    @Test
    @Order(8)
    public void testParseWithNoVerification() {
        if (testAccessToken == null) return;
        JwtPayload payload = jwtUtils.parseWithNoVerification(testAccessToken);
        Assertions.assertNotNull(payload);
        Assertions.assertEquals(TEST_PAYLOAD.getUserId(), payload.getUserId());
    }

    @Test
    @Order(9)
    public void testInvalidTokenThrowsException() {
        Assertions.assertThrows(Exception.class, () -> {
            jwtUtils.parserAccessToken("invalid.token.here");
        });
    }

    @Test
    @Order(10)
    public void testPasswordConsistency() {
        String password = "MySecurePass123!";
        String hash1 = PasswordUtils.encryptPassword(password);
        String hash2 = PasswordUtils.encryptPassword(password);
        Assertions.assertNotEquals(hash1, hash2);
        Assertions.assertTrue(PasswordUtils.verifyPassword(password, hash1));
        Assertions.assertTrue(PasswordUtils.verifyPassword(password, hash2));
    }
}
