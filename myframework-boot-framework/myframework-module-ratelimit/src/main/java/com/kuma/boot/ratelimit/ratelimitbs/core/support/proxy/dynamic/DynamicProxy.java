/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.dynamic;

import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.AbstractProxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy
extends AbstractProxy
implements InvocationHandler {
    private final Object target;

    public DynamicProxy(Object target) {
        this.target = target;
    }

    public DynamicProxy(Object target, RateLimitBs bs) {
        super(bs);
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.rateLimitBs.tryAcquire(method, args);
        return method.invoke(this.target, args);
    }

    public Object proxy() {
        DynamicProxy handler = new DynamicProxy(this.target);
        return Proxy.newProxyInstance(handler.getClass().getClassLoader(), this.target.getClass().getInterfaces(), handler);
    }
}

