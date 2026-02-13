/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash;

import java.util.Map;

public interface ConsistentHashing<T> {
    public T get(String var1);

    public ConsistentHashing add(T var1);

    public ConsistentHashing remove(T var1);

    public Map<Integer, T> nodeMap();
}

