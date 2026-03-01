/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.session;

import com.kuma.boot.dingtalk.model.DingerHandleProxy;

import java.lang.reflect.Proxy;

public class DefaultDingerSession
implements DingerSession {
    private final SessionConfiguration sessionConfiguration;

    public DefaultDingerSession(SessionConfiguration sessionConfiguration) {
        this.sessionConfiguration = sessionConfiguration;
    }

    @Override
    public <T> T getDinger(Class<T> type) {
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{type}, new DingerHandleProxy(this.configuration()));
    }

    @Override
    public SessionConfiguration configuration() {
        return this.sessionConfiguration;
    }
}

