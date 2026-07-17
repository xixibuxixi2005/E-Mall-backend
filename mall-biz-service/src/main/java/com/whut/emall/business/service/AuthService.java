package com.whut.emall.business.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.whut.emall.business.dto.RegisterDTO;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.business.vo.LoginVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;
import com.whut.emall.common.utils.PasswordUtils;

import jakarta.annotation.Resource;

@Service
public class AuthService {
    @Resource MemberService memberService;
    @Resource SysUserService sysUserService;
    @Resource JavaMailSender javaMailSender;
    @Resource JwtUtils jwtUtils;
    @Resource RedisTemplate<String, Object> redisTemplate;

    static final String SENDCODE="EMALL:SENDCODE:";
    static final String REGDCODE="EMALL:REGCODE:";
    
    private void redisSet(String type, String email, Object value, Duration timeout) {
        redisTemplate.boundValueOps(type+email)
            .set(value, timeout);
    }
    private Object redisGet(String type, String email) {
        return redisTemplate.boundValueOps(type+email).get();
    }
    private Boolean redisHas(String type, String email) {
        return redisTemplate.hasKey(type+email);
    }
    private void redisDel(String type, String email) {
        redisTemplate.delete(type+email);
    }

    Random random = new Random();

    public void register(RegisterDTO dto) {
        String email = dto.getEmail();
        if (memberService.getMemberByEmail(email)!=null)
            throw ApiException.err(400, "该邮箱已注册");
        if (!dto.getVerificationCode().equals(redisGet(REGDCODE, email)))
            throw ApiException.err(401, "验证码错误");
        Member member = new Member();
        member.setEmail(email);
        member.setPhone(dto.getPhone());
        member.setPassword(PasswordUtils.encryptPassword(dto.getPassword()));
        member.setUsername(dto.getUsername());
        memberService.addMember(member);
        redisDel(REGDCODE, email);
    }

    @Value("${spring.mail.username}") String mailFrom;
    public void sendCode(String email) {
        if (memberService.getMemberByEmail(email)!=null)
            throw ApiException.err(400, "该邮箱已注册");
        if (redisHas(SENDCODE, email))
            throw ApiException.err(429, "发送过于频繁，请稍后再试");
        String code = String.format("%06d", random.nextInt(1000000));
        redisSet(REGDCODE, email, code, Duration.ofSeconds(300));
        redisSet(SENDCODE, email, 1, Duration.ofSeconds(60));
        
        System.out.println("验证码："+code);
        MimeMessageHelper message = new MimeMessageHelper(javaMailSender.createMimeMessage());
        try {
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("E-Mall 注册验证");
            message.setText("你的验证码为：\n"+
                "<h2>"+code+"</h2>", true);
            javaMailSender.send(message.getMimeMessage());
        } catch (Exception err) {
            throw ApiException.err(err.getLocalizedMessage());
        }
    }

    public LoginVO login(String email, String password) {
        SysUser sysUser = sysUserService.getSysUserByEmail(email);
        if (sysUser != null) {
            if(!PasswordUtils.verifyPassword(password, sysUser.getPassword()))
                throw ApiException.err(401, "用户名或密码错误");
            if (sysUser.getStatus() == UserStatus.INVALID)
                throw ApiException.err(403, "账号已被禁用");
            JwtPayload payload = new JwtPayload(sysUser.getId(), sysUser.getEmail(), sysUser.getRoleCode());
            return new LoginVO(
                jwtUtils.makeAccessToken(payload),
                jwtUtils.makeRefreshToken(payload),
                sysUser.getUsername(),
                sysUser.getRoleCode(),
                sysUser.getId(),
                sysUser.getPhone()
            );
        } else {
            Member member = memberService.getMemberByEmail(email);
            if (member == null || !PasswordUtils.verifyPassword(password, member.getPassword()))
                throw ApiException.err(401, "用户名或密码错误");
            if (member.getStatus() == UserStatus.INVALID)
                throw ApiException.err(403, "账号已被禁用");
            JwtPayload payload = new JwtPayload(member.getId(), member.getEmail(), "MEMBER");
            return new LoginVO(
                jwtUtils.makeAccessToken(payload),
                jwtUtils.makeRefreshToken(payload),
                member.getUsername(),
                "MEMBER",
                member.getId(),
                member.getPhone()
            );
        }
    }

    public String refresh(String token, String refreshToken) {
        JwtPayload tokenJWT = jwtUtils.parseWithNoVerification(token);
        JwtPayload refreshJWT = jwtUtils.parserRefreshToken(refreshToken);
        if (!tokenJWT.equals(refreshJWT))
            throw ApiException.err(401, "无效refreshToken！");
        return jwtUtils.makeAccessToken(tokenJWT);
    }
}
