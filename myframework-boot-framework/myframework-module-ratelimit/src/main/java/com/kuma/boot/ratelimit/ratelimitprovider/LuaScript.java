/*
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StreamUtils
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

public final class LuaScript {
    private static final Logger log = LoggerFactory.getLogger(LuaScript.class);
    private static final String RATE_LIMITER_FILE_PATH = "META-INF/ratelimiter-spring-boot-starter-rateLimit.lua";
    private static String rateLimiterScript;

    private LuaScript() {
    }

    public static String getRateLimiterScript() {
        return "local rateLimitKey = KEYS[1];\nlocal rate = tonumber(KEYS[2]);\nlocal rateInterval = tonumber(KEYS[3]);\nlocal limitResult = 0;\nlocal ttlResult = 0;\nlocal currValue = redis.call('incr', rateLimitKey);\n\nif (currValue == 1) then\n    redis.call('expire', rateLimitKey, rateInterval);\n    limitResult = 0;\nelse\n    if (currValue > rate) then\n        limitResult = 1;\n        ttlResult = redis.call('ttl', rateLimitKey);\n    end\nend\n\nreturn { limitResult, ttlResult }\n";
    }

    static {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(RATE_LIMITER_FILE_PATH);
        try {
            rateLimiterScript = StreamUtils.copyToString((InputStream)in, (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

