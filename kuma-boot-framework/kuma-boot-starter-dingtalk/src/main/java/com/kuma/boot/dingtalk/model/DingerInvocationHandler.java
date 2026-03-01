/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import java.lang.reflect.InvocationHandler;

public abstract class DingerInvocationHandler
extends DingerMessageHandler
implements InvocationHandler {
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

