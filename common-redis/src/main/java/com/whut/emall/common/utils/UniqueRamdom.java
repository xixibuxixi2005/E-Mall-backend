package com.whut.emall.common.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class UniqueRamdom {
    @Resource RedisTemplate<String, Object> redisTemplate;
    final static String UNIQUE_KEY = "EMALL:UNIQUE";
    final static String UNIQUE_DAY_KEY = "EMALL:UNIQUE:DAY";

    public String getUniqueLabel(int length) {
        Long uniqueNum = redisTemplate.opsForValue().increment(UNIQUE_KEY);
        return String.format("%0"+length+"d", uniqueNum);
    }
    private String getDailyNumLabel(int length, String key) {
        var ops = redisTemplate.boundValueOps(key);
        Long uniqueNum = ops.increment();
        if (uniqueNum==1) {
            var expireTime = LocalDate.now().plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
            ops.expireAt(expireTime);
        }
        return String.format("%0"+length+"d", uniqueNum);
    }

    public String getDailyNumLabel(int length) {
        return getDailyNumLabel(length, UNIQUE_DAY_KEY);
    }
    public String getUniqueDateLabel(int length, String prefix) {
        return String.format("%s%s%s",
            prefix,
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            getDailyNumLabel(length, prefix)
        );
    }
}
