/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.utils.log.LogUtils;

public class ThreadPoolUncaughtExceptionHandler
implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler lastUncaughtExceptionHandler;

    public ThreadPoolUncaughtExceptionHandler(Thread.UncaughtExceptionHandler lastUncaughtExceptionHandler) {
        this.lastUncaughtExceptionHandler = lastUncaughtExceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e != null) {
            LogUtils.error(e, "[\u8b66\u544a] [{}] \u6355\u83b7\u9519\u8bef", Thread.currentThread().getName());
        }
        if (this.lastUncaughtExceptionHandler != null) {
            this.lastUncaughtExceptionHandler.uncaughtException(t, e);
        }
    }
}

