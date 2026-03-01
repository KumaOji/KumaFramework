/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * lua脚本
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:56:57
 */
public final class LuaScript {

    private LuaScript() {}

    private static final Logger log = LoggerFactory.getLogger(LuaScript.class);
    private static final String RATE_LIMITER_FILE_PATH = "META-INF/ratelimiter-spring-boot-starter-rateLimit.lua";
    private static String rateLimiterScript;

    static {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(RATE_LIMITER_FILE_PATH);
        try {
            rateLimiterScript = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException ignored) {

        }
    }

    public static String getRateLimiterScript() {
        // return rateLimiterScript;

        return """
			local rateLimitKey = KEYS[1];
			local rate = tonumber(KEYS[2]);
			local rateInterval = tonumber(KEYS[3]);
			local limitResult = 0;
			local ttlResult = 0;
			local currValue = redis.call('incr', rateLimitKey);

			if (currValue == 1) then
			    redis.call('expire', rateLimitKey, rateInterval);
			    limitResult = 0;
			else
			    if (currValue > rate) then
			        limitResult = 1;
			        ttlResult = redis.call('ttl', rateLimitKey);
			    end
			end

			return { limitResult, ttlResult }
			""";
    }
}
