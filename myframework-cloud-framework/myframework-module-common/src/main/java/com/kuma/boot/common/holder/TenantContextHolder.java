/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.TransmittableThreadLocal
 */
package com.kuma.boot.common.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

public class TenantContextHolder {
    private static final ThreadLocal<String> TENANT_CONTEXT = new TransmittableThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void setTenant(String tenant) {
        TENANT_CONTEXT.set(tenant);
    }

    public static String getTenant() {
        return TENANT_CONTEXT.get();
    }

    public static void clear() {
        TENANT_CONTEXT.remove();
    }

    public static String getDatabase() {
        return null;
    }

    public static void clearDatabase() {
    }
}

