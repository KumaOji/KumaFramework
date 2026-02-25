/*
 *  com.kuma.boot.common.support.proxy.Proxy
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy;

import com.kuma.boot.common.support.proxy.Proxy;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;

public abstract class AbstractProxy
implements Proxy {
    protected final RateLimitBs rateLimitBs;

    protected AbstractProxy(RateLimitBs rateLimitBs) {
        this.rateLimitBs = rateLimitBs;
    }

    public AbstractProxy() {
        this.rateLimitBs = RateLimitBs.newInstance();
    }
}

