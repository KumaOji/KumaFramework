/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.utils.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FatalExitExceptionHandler
implements Thread.UncaughtExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FatalExitExceptionHandler.class);
    public static final FatalExitExceptionHandler INSTANCE = new FatalExitExceptionHandler();
    public static final int EXIT_CODE = -17;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOG.error("FATAL: Thread '{}' produced an uncaught exception. Stopping the process...", (Object)t.getName(), (Object)e);
    }
}

