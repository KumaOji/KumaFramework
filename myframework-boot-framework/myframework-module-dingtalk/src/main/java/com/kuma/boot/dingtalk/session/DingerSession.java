/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.session;

public interface DingerSession {
    public <T> T getDinger(Class<T> var1);

    public SessionConfiguration configuration();
}

