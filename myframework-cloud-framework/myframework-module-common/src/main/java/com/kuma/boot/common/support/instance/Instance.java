/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.instance;

public interface Instance {
    public <T> T singleton(Class<T> var1, String var2);

    public <T> T singleton(Class<T> var1);

    public <T> T threadLocal(Class<T> var1);

    public <T> T multiple(Class<T> var1);

    public <T> T threadSafe(Class<T> var1);
}

