package com.kuma.boot.ratelimit.ratelimitrule;

import cn.hutool.core.lang.Snowflake;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Redis 多规则限流（未完成，暂不注册为 Spring Bean）
 *
 * @author YiFei
 * @since 2024/5/10 20:43
 */
@Aspect
@Order(20)
public class RateLimitersAspect {

    private final RedisRepository redisUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> limitScript;
    private final Snowflake snowflake;

    public RateLimitersAspect(RedisRepository redisUtil,
                              RedisTemplate<String, Object> redisTemplate, RedisScript<Boolean> limitScript,
                              Snowflake snowflake) {
        this.redisUtil = redisUtil;
        this.redisTemplate = redisTemplate;
        this.limitScript = limitScript;
        this.snowflake = snowflake;
    }

    @Before(value = "@annotation(rateLimiters)")
    public void boBefore(JoinPoint joinPoint, com.kuma.boot.ratelimit.ratelimitrule.RateLimiters rateLimiters) {
//        if (SecurityUtil.isAdmin()) {
//            return;
//        }
        com.kuma.boot.ratelimit.ratelimitrule.RateLimiter[] limiters = rateLimiters.rateLimiters();
        for (com.kuma.boot.ratelimit.ratelimitrule.RateLimiter limiter : limiters) {
            // 1. 生成限流key
//            String limitKey = generateLimiterKey(joinPoint, limiter);
//            // 2. 执行脚本返回是否限流成功 (传入key，唯一标识,当前时间 )
//            Boolean execute = redisTemplate.execute(limitScript, List.of(
//                    limitKey, snowflake.nextIdStr(), String.valueOf(System.currentTimeMillis())
//            ), getRules(limiter));
//            // 3. 判断是否限流
//            if (Boolean.TRUE.equals(execute)) {
//                // 3.1 是否加入黑名单
//                if (limiter.addToBlacklist()) {
//                    this.addToRedisBlackList();
//                }
//                // 3.2 抛出限流错误
//                throw new ServiceException(ResultCode.REQUEST_RATE_LIMIT);
//            }
        }
    }

    /**
     * 生成限流key
     */
    private String generateLimiterKey(JoinPoint joinPoint, com.kuma.boot.ratelimit.ratelimitrule.RateLimiter limiter) {
//        StringBuilder key = new StringBuilder(RedisKeyConstants.RATE_LIMIT_CACHE_PREFIX);
        StringBuilder key = new StringBuilder("");
        switch (limiter.limitTypeEnum()) {
            case IP -> {
                // 1. IP 模式
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
//                key.append(IpUtil.getIpAddr(request)).append(":");
            }
            case USER_ID -> {
                // 2. userId 模式
//                Long userId = SecurityUtil.getUserId();
//                if (userId != null) {
//                    key.append(userId).append(":");
//                } else {
//                    log.error("RateLimitersAspect.generateLimiterKey() => 无法获取到Id");
//                    throw new ServiceException(ResultCode.AUTH_TOKEN_INVALID);
//                }
            }
            case USER_NAME -> {
                // 3. userId 模式
//                String username = SecurityUtil.getUsername();
//                if (StringUtils.hasText(username)) {
//                    key.append(username).append(":");
//                } else {
//                    log.error("RateLimitersAspect.generateLimiterKey() => 无法获取到用户名");
//                    throw new ServiceException(ResultCode.AUTH_TOKEN_INVALID);
//                }
            }
            default -> {
                // 4. 默认全局模式
            }
        }
        // 拼接 role:类名:方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        key.append(targetClass.getSimpleName()).append(":").append(method.getName());
        return key.toString();
    }

    /**
     * 获取规则集合
     */
    private Object[] getRules(com.kuma.boot.ratelimit.ratelimitrule.RateLimiter limiter) {
        RateRule[] rateRules = limiter.rateRules();
        // 1. 创建返回对象 ( i * 2 === i << 1)
        Object[] result = new Object[rateRules.length * 2];
        // 2. 遍历规则返回
        for (int i = 0; i < rateRules.length; i++) {
            result[i * 2] = rateRules[i].limit();
            result[i * 2 + 1] = rateRules[i].timeUnit().toMillis(rateRules[i].timeDuration());
        }
        // 3. 返回结果
        return result;
    }

    /**
     * 添加到 redis 黑名单操作
     */
    private void addToRedisBlackList() {
        // http 请求不会为空
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
//        String ipAddr = IpUtil.getIpAddr(request);
//        Long userId = SecurityUtil.getUserId();
//        // 3.1.1 用户Id 和 ip 不为空则存入缓存
//        if (userId != null) {
//            redisUtil.addToCacheSet(RedisKeyConstants.BLACKLIST_USER_ID_CACHE_PREFIX, userId);
//        }
//        redisUtil.addToCacheSet(RedisKeyConstants.BLACKLIST_IP_CACHE_PREFIX, ipAddr);
    }
}
