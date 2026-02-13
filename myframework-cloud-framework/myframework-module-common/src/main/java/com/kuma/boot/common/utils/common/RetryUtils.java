/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.List;
import java.util.function.Consumer;

public final class RetryUtils {
    private static final String NAME = "\u91cd\u8bd5\u5de5\u5177";

    private RetryUtils() {
    }

    public static <R> R invoke(Supplier<R> dataSupplier, Consumer<Throwable> exceptionCaught, int retryCount, long sleepTime, List<Class<? extends Throwable>> expectExceptions) {
        try {
            return dataSupplier == null ? null : (R)dataSupplier.get();
        }
        catch (Throwable throwable) {
            RetryUtils.catchException(exceptionCaught, throwable);
            Throwable ex = throwable;
            if (expectExceptions != null && !expectExceptions.isEmpty()) {
                Class<?> exClass = ex.getClass();
                boolean match = expectExceptions.stream().anyMatch(clazz -> clazz == exClass);
                if (!match) {
                    return null;
                }
            }
            for (int i = 0; i < retryCount; ++i) {
                try {
                    if (sleepTime > 0L) {
                        Thread.sleep(sleepTime);
                    }
                    return dataSupplier.get();
                }
                catch (InterruptedException e) {
                    LogUtils.error("thread interrupted !! break retry,cause:" + e.getMessage(), new Object[0]);
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (Throwable throwable2) {
                    RetryUtils.catchException(exceptionCaught, throwable2);
                    continue;
                }
            }
            return null;
        }
    }

    private static void catchException(Consumer<Throwable> exceptionCaught, Throwable throwable) {
        try {
            if (exceptionCaught != null) {
                exceptionCaught.accept(throwable);
            }
        }
        catch (Throwable e) {
            LogUtils.error(e, "retry exception caught throw error:" + ExceptionUtils.getFullStackTrace(e), new Object[0]);
        }
    }

    @FunctionalInterface
    public static interface Supplier<T> {
        public T get() throws Exception;
    }
}

