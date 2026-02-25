/*
 *  com.google.common.collect.Lists
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.data.redis.core.script.DefaultRedisScript
 *  org.springframework.scripting.ScriptSource
 *  org.springframework.scripting.support.ResourceScriptSource
 */
package com.kuma.boot.ratelimit.ratelimitenhance.helper;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.ratelimit.ratelimitenhance.exception.EnhanceRedisLimitException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

public class RedisLimitHelper {
    private static final String LIMIT_TIME_PREFIX = "limit:slide-window:";
    private static final String BUCKET_LIMIT_KEY_PREFIX = "limit:token-bucket:";
    private static final String SLIDING_WINDOW_LIMIT_SCRIPT_LUA = "script/lua/sliding-window-limit.lua";
    private static final String TOKEN_BUCKET_LIMIT_SCRIPT_LUA = "script/lua/token-bucket-flow-limiting.lua";
    private static final DefaultRedisScript<Boolean> SLIDING_WINDOW_LIMIT_SCRIPT = new DefaultRedisScript();
    private static final DefaultRedisScript<Boolean> TOKEN_BUCKET_LIMIT_SCRIPT;
    RedisHelper redisHelper;

    public RedisLimitHelper(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    public Boolean tokenLimit(String limitKey, int capacity, int permits, double rate) {
        if (StringUtils.isBlank((String)limitKey)) {
            throw new EnhanceRedisLimitException("limitKey can not be null, please check param.");
        }
        if (capacity <= 0) {
            throw new EnhanceRedisLimitException("capacity must be greater than 0, please check param.");
        }
        if (permits <= 0) {
            throw new EnhanceRedisLimitException("permits must be greater than 0, please check param.");
        }
        if (rate <= 0.0) {
            throw new EnhanceRedisLimitException("rate must be greater than 0, please check param.");
        }
        ArrayList keys = Lists.newArrayListWithCapacity((int)1);
        String bucket = BUCKET_LIMIT_KEY_PREFIX + limitKey;
        keys.add(bucket);
        ArrayList<Object> args = new ArrayList<Object>();
        args.add(String.valueOf(capacity));
        args.add(String.valueOf(permits));
        double rateNum = rate / 1000.0;
        args.add(String.valueOf(rateNum));
        long currentTime = System.currentTimeMillis();
        args.add(String.valueOf(currentTime));
        return this.redisHelper.executeScript(TOKEN_BUCKET_LIMIT_SCRIPT, keys, (List<Object>)args);
    }

    public Boolean windowLimit(String limitKey, int maxRequest, int windowLength) {
        if (StringUtils.isBlank((String)limitKey)) {
            throw new EnhanceRedisLimitException("limitKey can not be null, please check param.");
        }
        if (maxRequest <= 0) {
            throw new EnhanceRedisLimitException("maxRequest must be greater than 0, please check param.");
        }
        if (windowLength <= 0) {
            throw new EnhanceRedisLimitException("windowLength must be greater than 0, please check param.");
        }
        ArrayList<String> keys = new ArrayList<String>();
        keys.add(LIMIT_TIME_PREFIX + limitKey);
        ArrayList<Object> args = new ArrayList<Object>();
        args.add(String.valueOf(maxRequest));
        args.add(String.valueOf(System.currentTimeMillis()));
        args.add(String.valueOf(windowLength *= 1000));
        return this.redisHelper.executeScript(SLIDING_WINDOW_LIMIT_SCRIPT, (List<String>)keys, (List<Object>)args);
    }

    static {
        SLIDING_WINDOW_LIMIT_SCRIPT.setScriptSource((ScriptSource)new ResourceScriptSource((Resource)new ClassPathResource(SLIDING_WINDOW_LIMIT_SCRIPT_LUA)));
        SLIDING_WINDOW_LIMIT_SCRIPT.setResultType(Boolean.class);
        TOKEN_BUCKET_LIMIT_SCRIPT = new DefaultRedisScript();
        TOKEN_BUCKET_LIMIT_SCRIPT.setScriptSource((ScriptSource)new ResourceScriptSource((Resource)new ClassPathResource(TOKEN_BUCKET_LIMIT_SCRIPT_LUA)));
        TOKEN_BUCKET_LIMIT_SCRIPT.setResultType(Boolean.class);
    }
}

