/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.data.redis.connection.RedisStringCommands$SetOption
 *  org.springframework.data.redis.connection.ReturnType
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.types.Expiration
 */
package com.kuma.boot.cache.redis.lock;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

@ConditionalOnClass(value={RedisTemplate.class})
@Deprecated
public class RedisDistributedLock {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ThreadLocal<String> lockFlag = new ThreadLocal();
    private static final String UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then     return redis.call(\"del\",KEYS[1]) else     return 0 end ";

    public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        boolean result = this.setRedis(key, expire);
        while (!result && retryTimes-- > 0) {
            try {
                LogUtils.debug((String)("get redisDistributeLock failed, retrying..." + retryTimes), (Object[])new Object[0]);
                Thread.sleep(sleepMillis);
            }
            catch (InterruptedException e) {
                LogUtils.error((String)"Interrupted!", (Object[])new Object[]{e});
                Thread.currentThread().interrupt();
            }
            result = this.setRedis(key, expire);
        }
        return result;
    }

    private boolean setRedis(String key, long expire) {
        try {
            return Boolean.TRUE.equals(this.redisTemplate.execute(connection -> {
                String uuid = UUID.randomUUID().toString();
                this.lockFlag.set(uuid);
                byte[] keyByte = this.redisTemplate.getStringSerializer().serialize((Object)key);
                byte[] uuidByte = this.redisTemplate.getStringSerializer().serialize((Object)uuid);
                return Boolean.TRUE.equals(connection.set(keyByte, uuidByte, Expiration.from((long)expire, (TimeUnit)TimeUnit.MILLISECONDS), RedisStringCommands.SetOption.ifAbsent()));
            }));
        }
        catch (Exception e) {
            LogUtils.error((String)"set redisDistributeLock occured an exception", (Object[])new Object[]{e});
            return false;
        }
    }

    public boolean releaseLock(String key) {
        try {
            boolean bl = (Boolean)this.redisTemplate.execute(connection -> {
                byte[] scriptByte = this.redisTemplate.getStringSerializer().serialize((Object)UNLOCK_LUA);
                return (Boolean)connection.eval(scriptByte, ReturnType.BOOLEAN, 1, (byte[][])new byte[][]{this.redisTemplate.getStringSerializer().serialize((Object)key), this.redisTemplate.getStringSerializer().serialize((Object)this.lockFlag.get())});
            });
            return bl;
        }
        catch (Exception e) {
            LogUtils.error((String)"release redisDistributeLock occured an exception", (Object[])new Object[]{e});
        }
        finally {
            this.lockFlag.remove();
        }
        return false;
    }
}

