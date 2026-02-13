/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.ttl.TransmittableThreadLocal
 */
package com.kuma.boot.common.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.common.model.BaseSecurityUser;

public class UserContextHolder {
    private static final ThreadLocal<BaseSecurityUser> LOGIN_USER_CONTEXT = new TransmittableThreadLocal();

    private UserContextHolder() {
    }

    public static void setUser(BaseSecurityUser user) {
        LOGIN_USER_CONTEXT.set(user);
    }

    public static BaseSecurityUser getUser() {
        return LOGIN_USER_CONTEXT.get();
    }

    public static Long getUserId() {
        return LOGIN_USER_CONTEXT.get().getUserId();
    }

    public static String getUserName() {
        return LOGIN_USER_CONTEXT.get().getUsername();
    }

    public static void clear() {
        LOGIN_USER_CONTEXT.remove();
    }
}

