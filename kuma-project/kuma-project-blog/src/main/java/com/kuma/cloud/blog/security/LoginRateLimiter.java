package com.kuma.cloud.blog.security;

import com.kuma.boot.common.exception.BusinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 登录接口 IP 速率限制：60 秒内同一 IP 最多尝试 5 次
 *
 * @author Kuma
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 60;
    private static final String KEY_PREFIX = "blog:login:attempts:";

    private final StringRedisTemplate redisTemplate;

    public LoginRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void check(String ip) {
        String key = KEY_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        if (count != null && count > MAX_ATTEMPTS) {
            throw new BusinessException("登录尝试过于频繁，请 " + WINDOW_SECONDS + " 秒后再试");
        }
    }

    /**
     * 登录成功后清除计数，避免正常用户累积
     */
    public void reset(String ip) {
        redisTemplate.delete(KEY_PREFIX + ip);
    }

    /**
     * 优先取反向代理真实 IP，回退到 RemoteAddr
     */
    public static String resolveIp(jakarta.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能是逗号分隔的多个 IP，取第一个
            int commaIndex = ip.indexOf(',');
            return commaIndex > 0 ? ip.substring(0, commaIndex).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }
}
