/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.TransmittableThreadLocal
 */
package com.kuma.boot.common.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

public class VersionContextHolder {
    private static final ThreadLocal<String> VERSION_CONTEXT = new TransmittableThreadLocal();

    private VersionContextHolder() {
    }

    public static void setVersion(String version) {
        VERSION_CONTEXT.set(version);
    }

    public static String getVersion() {
        return VERSION_CONTEXT.get();
    }

    public static void clear() {
        VERSION_CONTEXT.remove();
    }
}

