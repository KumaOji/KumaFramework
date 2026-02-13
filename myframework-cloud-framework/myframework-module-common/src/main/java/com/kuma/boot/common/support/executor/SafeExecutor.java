/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.executor;

import com.kuma.boot.common.support.executor.CallableWithParams;
import com.kuma.boot.common.support.executor.VoidCallable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SafeExecutor {
    public static final Consumer<Exception> IGNORE_EXCEPTION_HANDLER = e -> {};

    private SafeExecutor() {
    }

    public static void executeWithoutException(VoidCallable callable) {
        SafeExecutor.execute(callable, IGNORE_EXCEPTION_HANDLER);
    }

    public static void execute(VoidCallable callable, Consumer<Exception> exceptionHandler) {
        try {
            callable.call();
        }
        catch (Exception e) {
            SafeExecutor.handleException(e, exceptionHandler);
        }
    }

    public static <T> T supplyWithoutException(Supplier<T> supplier) {
        return SafeExecutor.supplyWithoutException(supplier, null, IGNORE_EXCEPTION_HANDLER);
    }

    public static <T> T supplyWithoutException(Supplier<T> supplier, T defaultValue) {
        return SafeExecutor.supplyWithoutException(supplier, defaultValue, IGNORE_EXCEPTION_HANDLER);
    }

    public static <T> T supplyWithoutException(Supplier<T> supplier, T defaultValue, Consumer<Exception> exceptionHandler) {
        try {
            return supplier.get();
        }
        catch (Exception e) {
            SafeExecutor.handleException(e, exceptionHandler);
            return defaultValue;
        }
    }

    public static void executeAsyncWithoutException(VoidCallable callable) {
        SafeExecutor.executeAsync(callable, IGNORE_EXCEPTION_HANDLER);
    }

    public static void executeAsync(VoidCallable callable, Consumer<Exception> exceptionHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                callable.call();
            }
            catch (Exception e) {
                SafeExecutor.handleException(e, exceptionHandler);
            }
        });
    }

    public static <T> CompletableFuture<T> supplyAsyncWithoutException(Supplier<T> supplier) {
        return SafeExecutor.supplyAsyncWithoutException(supplier, null, IGNORE_EXCEPTION_HANDLER);
    }

    public static <T> CompletableFuture<T> supplyAsyncWithoutException(Supplier<T> supplier, T defaultValue) {
        return SafeExecutor.supplyAsyncWithoutException(supplier, defaultValue, IGNORE_EXCEPTION_HANDLER);
    }

    public static <T> CompletableFuture<T> supplyAsyncWithoutException(Supplier<T> supplier, T defaultValue, Consumer<Exception> exceptionHandler) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            }
            catch (Exception e) {
                SafeExecutor.handleException(e, exceptionHandler);
                return defaultValue;
            }
        });
    }

    public static <R, P> R callWithoutException(CallableWithParams<R, P> callable, P ... params) {
        return SafeExecutor.callWithoutException(callable, null, IGNORE_EXCEPTION_HANDLER, params);
    }

    public static <R, P> R callWithoutException(CallableWithParams<R, P> callable, R defaultValue, Consumer<Exception> exceptionHandler, P ... params) {
        try {
            return callable.call(params);
        }
        catch (Exception e) {
            SafeExecutor.handleException(e, exceptionHandler);
            return defaultValue;
        }
    }

    public static <R, P> CompletableFuture<R> callAsyncWithoutException(CallableWithParams<R, P> callable, P ... params) {
        return SafeExecutor.callAsyncWithoutException(callable, null, IGNORE_EXCEPTION_HANDLER, params);
    }

    public static <R, P> CompletableFuture<R> callAsyncWithoutException(CallableWithParams<R, P> callable, R defaultValue, Consumer<Exception> exceptionHandler, P ... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call(params);
            }
            catch (Exception e) {
                SafeExecutor.handleException(e, exceptionHandler);
                return defaultValue;
            }
        });
    }

    private static void handleException(Exception e, Consumer<Exception> exceptionHandler) {
        if (Objects.nonNull(exceptionHandler)) {
            exceptionHandler.accept(e);
        }
    }
}

