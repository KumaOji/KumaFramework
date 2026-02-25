/*
 *  cn.hutool.core.util.StrUtil
 *  com.google.common.collect.ImmutableList
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.data.redis.core.script.DefaultRedisScript
 *  org.springframework.data.redis.core.script.RedisScript
 */
package com.kuma.boot.ratelimit.ratelimitaspect;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableList;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ratelimit.ratelimitguava.Limit;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Aspect
public class LimitAspect {
    private static final String UNKNOWN = "unknown";
    private final RedisRepository redisRepository;

    public LimitAspect(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Around(value="@annotation(limit)")
    public Object around(ProceedingJoinPoint pjp, Limit limit) {
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        Method method = signature.getMethod();
        LimitType limitType = limit.limitType();
        String name = limit.name();
        int limitPeriod = limit.period();
        int limitCount = limit.count();
        String key = switch (limitType) {
            default -> throw new MatchException(null, null);
            case LimitType.IP -> RequestUtils.getHttpServletRequestIpAddress();
            case LimitType.CUSTOMER -> StrUtil.isBlank((CharSequence)limit.key()) ? StringUtils.upperCase((String)method.getName()) : limit.key();
        };
        ImmutableList keys = ImmutableList.of((Object)com.kuma.boot.common.utils.lang.StringUtils.join((String)limit.prefix(), (Object[])new Object[]{key}));
        try {
            String luaScript = this.buildLuaScript();
            DefaultRedisScript redisScript = new DefaultRedisScript(luaScript, Number.class);
            Number count = (Number)this.redisRepository.getRedisTemplate().execute((RedisScript)redisScript, (List)keys, new Object[]{limitCount, limitPeriod});
            if (count != null && count.intValue() <= limitCount) {
                return pjp.proceed();
            }
            throw new LimitException(ResultEnum.BLACKLIST);
        }
        catch (Throwable e) {
            LogUtils.error((Throwable)e);
            throw new LimitException(ResultEnum.FAILED);
        }
    }

    public String buildLuaScript() {
        return "local c\nc = redis.call('get',KEYS[1])\n\n-- \u8c03\u7528\u4e0d\u8d85\u8fc7\u6700\u5927\u503c\uff0c\u5219\u76f4\u63a5\u8fd4\u56de\nif c and tonumber(c) > tonumber(ARGV[1]) then\n\treturn c;\nend\n\n-- \u6267\u884c\u8ba1\u7b97\u5668\u81ea\u52a0\nc = redis.call('incr',KEYS[1])\nif tonumber(c) == 1 then\n-- \u4ece\u7b2c\u4e00\u6b21\u8c03\u7528\u5f00\u59cb\u9650\u6d41\uff0c\u8bbe\u7f6e\u5bf9\u5e94\u952e\u503c\u7684\u8fc7\u671f\n\tredis.call('expire',KEYS[1],ARGV[2])\nend\n\nreturn c;\n";
    }
}

