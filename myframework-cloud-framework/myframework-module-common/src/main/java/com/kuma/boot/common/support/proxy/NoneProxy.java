/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.proxy;

import com.kuma.boot.common.support.proxy.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NoneProxy
implements InvocationHandler,
Proxy {
    private final Object target;

    public NoneProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object proxy() {
        return this.target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(proxy, args);
    }
}

