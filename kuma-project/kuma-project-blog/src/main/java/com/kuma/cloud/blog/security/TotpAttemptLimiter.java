package com.kuma.cloud.blog.security;

import com.kuma.boot.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * TOTP 动态验证码频率限制。
 *
 * 规则：每轮最多尝试 3 次，共 5 轮，每轮失败后锁定时长依次递增：
 *   第1轮 → 锁 1 分钟
 *   第2轮 → 锁 5 分钟
 *   第3轮 → 锁 30 分钟
 *   第4轮 → 锁 2 小时
 *   第5轮 → 永久锁定
 *
 * Redis key：
 *   blog:totp:state:{userId}  Hash  round / attempts（记录当前轮次和本轮已用次数）
 *   blog:totp:lock:{userId}   String  "locked" 或 "permanent"，带 TTL 或无 TTL
 */
@Component
@RequiredArgsConstructor
public class TotpAttemptLimiter {

    private static final String STATE_KEY = "blog:totp:state:";
    private static final String LOCK_KEY  = "blog:totp:lock:";

    private static final int    MAX_ATTEMPTS_PER_ROUND = 3;
    private static final int    MAX_ROUNDS             = 5;
    /** 各轮耗尽后的锁定时长（秒），-1 表示永久 */
    private static final long[] LOCK_DURATIONS         = {60, 300, 1800, 7200, -1};

    private final StringRedisTemplate redisTemplate;

    /**
     * 验证 TOTP 前调用。若账号已锁定则直接抛出异常。
     */
    public void checkLock(Long userId) {
        String val = redisTemplate.opsForValue().get(LOCK_KEY + userId);
        if (val == null) return;
        if ("permanent".equals(val)) {
            throw new BusinessException("动态验证码已永久锁定，请联系管理员");
        }
        Long ttl = redisTemplate.getExpire(LOCK_KEY + userId, TimeUnit.SECONDS);
        throw new BusinessException("动态验证码错误次数过多，请 " + formatDuration(ttl == null ? 0 : ttl) + " 后重试");
    }

    /**
     * TOTP 验证失败后调用。
     *
     * @return 本轮剩余次数（>0）；若本次失败触发锁定则直接抛出异常。
     */
    public int recordFailure(Long userId) {
        String stateKey = STATE_KEY + userId;

        Map<Object, Object> state = redisTemplate.opsForHash().entries(stateKey);
        int round    = parseOrDefault(state, "round",    1);
        int attempts = parseOrDefault(state, "attempts", 0) + 1;

        if (attempts >= MAX_ATTEMPTS_PER_ROUND) {
            if (round >= MAX_ROUNDS) {
                redisTemplate.opsForValue().set(LOCK_KEY + userId, "permanent");
                redisTemplate.delete(stateKey);
                throw new BusinessException("动态验证码已永久锁定，请联系管理员");
            }
            long lockSec = LOCK_DURATIONS[round - 1];
            redisTemplate.opsForValue().set(LOCK_KEY + userId, "locked", lockSec, TimeUnit.SECONDS);
            redisTemplate.opsForHash().put(stateKey, "round",    String.valueOf(round + 1));
            redisTemplate.opsForHash().put(stateKey, "attempts", "0");
            throw new BusinessException("动态验证码连续错误，已锁定 " + formatDuration(lockSec));
        }

        redisTemplate.opsForHash().put(stateKey, "round",    String.valueOf(round));
        redisTemplate.opsForHash().put(stateKey, "attempts", String.valueOf(attempts));
        return MAX_ATTEMPTS_PER_ROUND - attempts;
    }

    /**
     * TOTP 验证成功后调用，清除限制状态。
     */
    public void reset(Long userId) {
        redisTemplate.delete(STATE_KEY + userId);
        redisTemplate.delete(LOCK_KEY  + userId);
    }

    // ── 工具方法 ────────────────────────────────────────────────

    private int parseOrDefault(Map<Object, Object> map, String key, int def) {
        Object val = map.get(key);
        if (val == null) return def;
        try { return Integer.parseInt(val.toString()); } catch (NumberFormatException e) { return def; }
    }

    private String formatDuration(long seconds) {
        if (seconds <= 0)   return "片刻";
        if (seconds < 60)   return seconds + " 秒";
        if (seconds < 3600) return (seconds / 60) + " 分钟";
        return (seconds / 3600) + " 小时";
    }
}
