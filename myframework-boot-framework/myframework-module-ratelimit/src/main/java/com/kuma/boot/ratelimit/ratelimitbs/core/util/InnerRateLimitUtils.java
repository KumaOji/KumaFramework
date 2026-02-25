/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.util;

import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;

public class InnerRateLimitUtils {
    private InnerRateLimitUtils() {
    }

    public static Long calcRate(RateLimitConfigDto configDto) {
        long intervalSeconds = configDto.getTimeUnit().toSeconds(configDto.getInterval());
        return Math.max(1L, configDto.getCount() / intervalSeconds);
    }
}

