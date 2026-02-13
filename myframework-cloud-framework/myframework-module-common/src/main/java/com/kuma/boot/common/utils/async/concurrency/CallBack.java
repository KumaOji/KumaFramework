/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async.concurrency;

public interface CallBack
extends Runnable {
    default public String getCallBackName() {
        return Thread.currentThread().threadId() + ":" + this.getClass().getName();
    }

    default public <T> void onSuccess(T result) {
    }

    default public void onError(Throwable error) {
    }
}

