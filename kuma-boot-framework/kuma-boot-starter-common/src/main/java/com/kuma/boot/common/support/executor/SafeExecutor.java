/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.executor;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 简化异常处理：通过封装异常处理逻辑，减少代码中的 try-catch 块，使代码更加简洁和易读。
 * 统一的异常处理策略：提供一种统一的方式来处理异常，允许指定自定义的异常处理器。 支持同步和异步任务：提供方法来执行同步和异步任务，并在发生异常时进行处理。
 * 默认值返回：在任务执行失败时，允许返回一个默认值，以保证程序的稳定性和连续性。 支持多参数和返回值的任务：处理带多个参数和返回值的任务，提供更高的灵活性和强大功能。
 *
 * 作者：Derek_Smart 链接：https://juejin.cn/post/7473051800095965235 来源：稀土掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。 提供了一组静态方法来安全地执行同步和异步任务。
 * 这些方法在执行过程中捕获异常，并通过指定的异常处理器处理异常。 该类提供了无返回值和有返回值的任务执行方法，并支持默认值和异常处理器的自定义。
 *
 * @Author derek_smart
 * @since 2025/2/18 8:50
 */
public final class SafeExecutor {

    /**
     * 默认的异常处理器，忽略所有异常。
     */
    public static final Consumer<Exception> IGNORE_EXCEPTION_HANDLER = e -> {};

    private SafeExecutor() {
        // Private constructor to prevent instantiation
    }

    // 同步方法

    /**
     * 执行一个无返回值的任务，并忽略任何异常。
     * @param callable 要执行的任务
     */
    public static void executeWithoutException(VoidCallable callable) {
        execute(callable, IGNORE_EXCEPTION_HANDLER);
    }

    /**
     * 执行一个无返回值的任务，并使用指定的异常处理器处理异常。
     * @param callable 要执行的任务
     * @param exceptionHandler 异常处理器
     */
    public static void execute(VoidCallable callable, Consumer<Exception> exceptionHandler) {
        try {
            callable.call();
        } catch (Exception e) {
            handleException(e, exceptionHandler);
        }
    }

    /**
     * 执行一个有返回值的任务，并忽略任何异常。如果发生异常，返回 null。
     * @param supplier 要执行的任务
     * @param <T> 返回值的类型
     * @return 任务的返回值，如果发生异常则返回 null
     */
    public static <T> T supplyWithoutException(Supplier<T> supplier) {
        return supplyWithoutException(supplier, null, IGNORE_EXCEPTION_HANDLER);
    }

    /**
     * 执行一个有返回值的任务，并忽略任何异常。如果发生异常，返回指定的默认值。
     * @param supplier 要执行的任务
     * @param defaultValue 默认返回值
     * @param <T> 返回值的类型
     * @return 任务的返回值，如果发生异常则返回默认值
     */
    public static <T> T supplyWithoutException(Supplier<T> supplier, T defaultValue) {
        return supplyWithoutException(supplier, defaultValue, IGNORE_EXCEPTION_HANDLER);
    }

    /**
     * 执行一个有返回值的任务，并使用指定的异常处理器处理异常。如果发生异常，返回指定的默认值。
     * @param supplier 要执行的任务
     * @param defaultValue 默认返回值
     * @param exceptionHandler 异常处理器
     * @param <T> 返回值的类型
     * @return 任务的返回值，如果发生异常则返回默认值
     */
    public static <T> T supplyWithoutException(
            Supplier<T> supplier, T defaultValue, Consumer<Exception> exceptionHandler) {
        try {
            return supplier.get();
        } catch (Exception e) {
            handleException(e, exceptionHandler);
        }
        return defaultValue;
    }

    /**
     * 异步执行一个无返回值的任务，并忽略任何异常。
     * @param callable 要执行的任务
     */
    public static void executeAsyncWithoutException(VoidCallable callable) {
        executeAsync(callable, IGNORE_EXCEPTION_HANDLER);
    }

    /**
     * 异步执行一个无返回值的任务，并使用指定的异常处理器处理异常。
     * @param callable 要执行的任务
     * @param exceptionHandler 异常处理器
     */
    public static void executeAsync(VoidCallable callable, Consumer<Exception> exceptionHandler) {
        CompletableFuture.runAsync(
                () -> {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        handleException(e, exceptionHandler);
                    }
                });
    }

    /**
     * 异步执行一个有返回值的任务，并忽略任何异常。如果发生异常，返回 null。
     * @param supplier 要执行的任务
     * @param <T> 返回值的类型
     * @return 一个 CompletableFuture，表示任务的返回值，如果发生异常则返回 null
     */
    public static <T> CompletableFuture<T> supplyAsyncWithoutException(Supplier<T> supplier) {
        return supplyAsyncWithoutException(supplier, null, IGNORE_EXCEPTION_HANDLER);
    }

    /**
     * 异步执行一个有返回值的任务，并忽略任何异常。如果发生异常，返回指定的默认值。
     * @param supplier 要执行的任务
     * @param defaultValue 默认返回值
     * @param <T> 返回值的类型
     * @return 一个 CompletableFuture，表示任务的返回值，如果发生异常则返回默认值
     */
    public static <T> CompletableFuture<T> supplyAsyncWithoutException(
            Supplier<T> supplier, T defaultValue) {
        return supplyAsyncWithoutException(supplier, defaultValue, IGNORE_EXCEPTION_HANDLER);
    }

    /**
     * 异步执行一个有返回值的任务，并使用指定的异常处理器处理异常。如果发生异常，返回指定的默认值。
     * @param supplier 要执行的任务
     * @param defaultValue 默认返回值
     * @param exceptionHandler 异常处理器
     * @param <T> 返回值的类型
     * @return 一个 CompletableFuture，表示任务的返回值，如果发生异常则返回默认值
     */
    public static <T> CompletableFuture<T> supplyAsyncWithoutException(
            Supplier<T> supplier, T defaultValue, Consumer<Exception> exceptionHandler) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return supplier.get();
                    } catch (Exception e) {
                        handleException(e, exceptionHandler);
                        return defaultValue;
                    }
                });
    }

    // 新增方法：处理带多个参数和返回值的任务

    /**
     * 执行一个带多个参数和返回值的任务，并忽略任何异常。
     * @param callable 要执行的任务
     * @param params 参数
     * @param <R> 返回值的类型
     * @param <P> 参数的类型
     * @return 任务的返回值，如果发生异常则返回 null
     */
    @SafeVarargs
    public static <R, P> R callWithoutException(CallableWithParams<R, P> callable, P... params) {
        return callWithoutException(callable, null, IGNORE_EXCEPTION_HANDLER, params);
    }

    /**
     * 执行一个带多个参数和返回值的任务，并使用指定的异常处理器处理异常。
     * @param callable 要执行的任务
     * @param defaultValue 默认返回值
     * @param exceptionHandler 异常处理器
     * @param params 参数
     * @param <R> 返回值的类型
     * @param <P> 参数的类型
     * @return 任务的返回值，如果发生异常则返回默认值
     */
    @SafeVarargs
    public static <R, P> R callWithoutException(
            CallableWithParams<R, P> callable,
            R defaultValue,
            Consumer<Exception> exceptionHandler,
            P... params) {
        try {
            return callable.call(params);
        } catch (Exception e) {
            handleException(e, exceptionHandler);
            return defaultValue;
        }
    }

    /**
     * 异步执行一个带多个参数和返回值的任务，并忽略任何异常。
     * @param callable 要执行的任务
     * @param params 参数
     * @param <R> 返回值的类型
     * @param <P> 参数的类型
     * @return 一个 CompletableFuture，表示任务的返回值，如果发生异常则返回 null
     */
    @SafeVarargs
    public static <R, P> CompletableFuture<R> callAsyncWithoutException(
            CallableWithParams<R, P> callable, P... params) {
        return callAsyncWithoutException(callable, null, IGNORE_EXCEPTION_HANDLER, params);
    }

    /**
     * 异步执行一个带多个参数和返回值的任务，并使用指定的异常处理器处理异常。
     * @param callable 要执行的任务
     * @param defaultValue 默认返回值
     * @param exceptionHandler 异常处理器
     * @param params 参数
     * @param <R> 返回值的类型
     * @param <P> 参数的类型
     * @return 一个 CompletableFuture，表示任务的返回值，如果发生异常则返回默认值
     */
    @SafeVarargs
    public static <R, P> CompletableFuture<R> callAsyncWithoutException(
            CallableWithParams<R, P> callable,
            R defaultValue,
            Consumer<Exception> exceptionHandler,
            P... params) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return callable.call(params);
                    } catch (Exception e) {
                        handleException(e, exceptionHandler);
                        return defaultValue;
                    }
                });
    }

    /**
     * 处理异常的方法。如果提供了异常处理器，则调用它来处理异常。
     * @param e 异常
     * @param exceptionHandler 异常处理器
     */
    private static void handleException(Exception e, Consumer<Exception> exceptionHandler) {
        if (Objects.nonNull(exceptionHandler)) {
            exceptionHandler.accept(e);
        }
    }
}
