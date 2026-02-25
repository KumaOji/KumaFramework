/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.api.support;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import java.util.List;

public interface IRateLimitRejectListenerContext
extends IRateLimitContext {
    public String tokenId();

    public String methodId();

    public List<RateLimitConfigDto> configList();

    public boolean acquireFlag();
}

