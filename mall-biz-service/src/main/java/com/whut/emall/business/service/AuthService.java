package com.whut.emall.business.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.whut.emall.business.dto.RegisterDTO;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.entity.SysUser;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;
import com.whut.emall.common.utils.PasswordUtils;

import jakarta.annotation.Resource;

@Service
public class AuthService {
    @Resource JwtUtils jwtUtils;
    @Resource MemberService memberService;
    @Resource SysUserService sysUserService;
    @Resource JavaMailSender javaMailSender;

    Random random = new Random();
    ConcurrentHashMap<String,String> registerCodes = new ConcurrentHashMap<>();
    ConcurrentHashMap<String,ScheduledFuture<?>> registerCodesTask = new ConcurrentHashMap<>();
    ConcurrentHashMap<String,ScheduledFuture<?>> sendCodesTask = new ConcurrentHashMap<>();
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    public void register(RegisterDTO dto) {
        String email = dto.getEmail();
        if (memberService.getMemberByEmail(email)!=null)
            throw ApiException.err(400, "该邮箱已注册");
        if (!dto.getVerificationCode().equals(registerCodes.get(email)))
            throw ApiException.err(401, "验证码错误");
        Member member = new Member();
        member.setEmail(email);
        member.setPhone(dto.getPhone());
        member.setPassword(PasswordUtils.encryptPassword(dto.getPassword()));
        member.setUsername(dto.getUsername());
        memberService.addMember(member);
    }

    @Value("${spring.mail.username}") String mailFrom;
    public void sendCode(String email) {
        if (memberService.getMemberByEmail(email)!=null)
            throw ApiException.err(400, "该邮箱已注册");
        if (sendCodesTask.containsKey(email))
            throw ApiException.err(429, "发送过于频繁，请稍后再试");
        String code = String.format("%06d", random.nextInt(1000000));
        registerCodes.put(email, code);

        if (registerCodesTask.containsKey(email)) {
            registerCodesTask.get(email).cancel(false);
        }
        registerCodesTask.put(email,
            executor.schedule(() -> {
                registerCodes.remove(email);
                registerCodesTask.remove(email);
            }, 300L, TimeUnit.SECONDS)
        );
        sendCodesTask.put(email,
            executor.schedule(() -> {
                sendCodesTask.remove(email);
            }, 60L, TimeUnit.SECONDS)
        );
        
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

    public Map<String,Object> login(String email, String password) {
        Map<String,Object> result = new HashMap<>();

        SysUser sysUser = sysUserService.getSysUserByEmail(email);
        if (sysUser != null) {
            if(!PasswordUtils.verifyPassword(password, sysUser.getPassword()))
                throw ApiException.err(401, "用户名或密码错误");
            if (sysUser.getStatus() == UserStatus.INVALID)
                throw ApiException.err(403, "账号已被禁用");
            JwtPayload payload = new JwtPayload(sysUser.getId(), sysUser.getEmail(), sysUser.getRoleCode());
            result.put("token", jwtUtils.makeAccessToken(payload));
            result.put("refreshToken", jwtUtils.makeRefreshToken(payload));
            result.put("username", sysUser.getUsername());
            result.put("roleCode", sysUser.getRoleCode());
            result.put("userId", sysUser.getId());
            result.put("phone", sysUser.getPhone());
        } else {
            Member member = memberService.getMemberByEmail(email);
            if (member == null || !PasswordUtils.verifyPassword(password, member.getPassword()))
                throw ApiException.err(401, "用户名或密码错误");
            if (member.getStatus() == UserStatus.INVALID)
                throw ApiException.err(403, "账号已被禁用");
            JwtPayload payload = new JwtPayload(member.getId(), member.getEmail(), "MEMBER");
            result.put("token", jwtUtils.makeAccessToken(payload));
            result.put("refreshToken", jwtUtils.makeRefreshToken(payload));
            result.put("username", member.getUsername());
            result.put("roleCode", "MEMBER");
            result.put("userId", member.getId());
            result.put("phone", member.getPhone());
        }
        return result;
    }

    public Map<String, String> refresh(String token, String refreshToken) {
        JwtPayload tokenJWT = jwtUtils.parserAccessToken(token);
        JwtPayload refreshJWT = jwtUtils.parserRefreshToken(refreshToken);
        if (!tokenJWT.equals(refreshJWT))
            throw ApiException.err(401, "无效refreshToken！");
        return Map.of("token", jwtUtils.makeAccessToken(tokenJWT));
    }
}
