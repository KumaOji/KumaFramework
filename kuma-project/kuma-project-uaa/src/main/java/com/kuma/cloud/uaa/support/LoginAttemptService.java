package com.kuma.cloud.uaa.support;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.uaa.config.UaaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 登录失败计数与临时锁定。计数落在 Redis 上并带滑动过期，
 * 达到阈值后在锁定窗口内直接拒绝该账号的登录请求，避免在线撞库。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final String KEY_PREFIX = "uaa:login:fail:";

    private final RedisRepository redisRepository;
    private final UaaProperties properties;

    public boolean isLocked(String username) {
        return currentFailCount(username) >= properties.getLogin().getMaxFailCount();
    }

    /**
     * @return 锁定剩余秒数，未锁定时返回 0
     */
    public long lockRemainingSeconds(String username) {
        if (!isLocked(username)) {
            return 0L;
        }
        Long expire = redisRepository.getExpire(key(username));
        return expire == null || expire < 0 ? properties.getLogin().getLockDuration().toSeconds() : expire;
    }

    public void recordFailure(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        String key = key(username);
        Long count = redisRepository.incr(key, 1L);
        // 首次失败时才设置过期，保证锁定窗口从第一次失败开始计算
        if (count != null && count == 1L) {
            redisRepository.expire(key, properties.getLogin().getLockDuration());
        }
        if (count != null && count >= properties.getLogin().getMaxFailCount()) {
            log.warn("账号 {} 连续登录失败 {} 次，已进入锁定窗口", username, count);
        }
    }

    public void reset(String username) {
        if (StringUtils.hasText(username)) {
            redisRepository.del(key(username));
        }
    }

    private long currentFailCount(String username) {
        if (!StringUtils.hasText(username)) {
            return 0L;
        }
        Object value = redisRepository.get(key(username));
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException exception) {
            log.warn("登录失败计数值异常，按未锁定处理: username={}, value={}", username, value);
            return 0L;
        }
    }

    private String key(String username) {
        return KEY_PREFIX + username;
    }
}
