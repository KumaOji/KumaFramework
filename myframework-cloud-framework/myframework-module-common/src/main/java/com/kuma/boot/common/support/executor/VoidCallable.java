/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.executor;

import java.io.Serializable;

public interface VoidCallable
extends Serializable {
    public void call() throws Exception;

    default public void callWithRuntimeException() {
        try {
            this.call();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default public void callWithoutException() {
        try {
            this.call();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static VoidCallable empty() {
        return () -> {};
    }
}

