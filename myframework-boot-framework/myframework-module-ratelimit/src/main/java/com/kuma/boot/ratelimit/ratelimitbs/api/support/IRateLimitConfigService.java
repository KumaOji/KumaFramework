/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.api.support;

import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import java.lang.reflect.Method;
import java.util.List;

public interface IRateLimitConfigService {
    public List<RateLimitConfigDto> queryConfigList(String var1, String var2, Method var3);
}

