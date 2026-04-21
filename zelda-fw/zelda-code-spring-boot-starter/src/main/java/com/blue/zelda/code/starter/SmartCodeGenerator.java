package com.blue.zelda.code.starter;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class SmartCodeGenerator {

    private final StringRedisTemplate redisTemplate;
    private final CodeGeneratorProperties prop;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public SmartCodeGenerator(StringRedisTemplate redisTemplate, CodeGeneratorProperties prop) {
        this.redisTemplate = redisTemplate;
        this.prop = prop;
    }

    public String generate(String prefix) {
        if (prefix == null || prefix.length() != 2) {
            throw new IllegalArgumentException("前缀必须是 2 位字符");
        }
        String date = LocalDateTime.now().format(DATE_FMT);
        String node = getAvailableNode(date, prefix);
        String seq = getSeq(date, node, prefix);
        String body = date + node + seq;
        int check = CheckCodeUtil.calcCheck(body);
        return prefix + body + check;
    }

    private String getAvailableNode(String date, String prefix) {
        for (int i = 1; i <= prop.getMaxNode(); i++) {
            String node = String.format("%02d", i);
            String key = "biz:seq:" + prefix + ":" + date + ":" + node;
            String val = redisTemplate.opsForValue().get(key);
            long curr = (val == null) ? 0 : Long.parseLong(val);
            if (curr < prop.getThreshold()) return node;
        }
        return String.format("%02d", prop.getMaxNode());
    }

    private String getSeq(String date, String node, String prefix) {
        String key = "biz:seq:" + prefix + ":" + date + ":" + node;
        Long num = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
        return String.format("%0" + prop.getSeqLength() + "d", num);
    }

    public boolean verify(String code) {
        if (code == null || code.length() != 17) return false;
        String body = code.substring(2, 16);
        int inputCheck = code.charAt(16) - '0';
        return CheckCodeUtil.calcCheck(body) == inputCheck;
    }
}
