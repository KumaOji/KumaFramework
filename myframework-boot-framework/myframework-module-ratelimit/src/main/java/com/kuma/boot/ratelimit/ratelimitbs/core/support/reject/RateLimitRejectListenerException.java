/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.reject;

import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListenerContext;
import com.kuma.boot.ratelimit.ratelimitbs.core.exception.RateLimitRuntimeException;

public class RateLimitRejectListenerException
implements IRateLimitRejectListener {
    @Override
    public void listen(IRateLimitRejectListenerContext context) {
        boolean acquireFlag = context.acquireFlag();
        if (!acquireFlag) {
            throw new RateLimitRuntimeException();
        }
    }
}

