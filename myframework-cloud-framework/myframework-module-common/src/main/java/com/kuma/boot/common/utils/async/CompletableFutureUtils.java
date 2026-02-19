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

package com.kuma.boot.common.utils.async;

import com.kuma.boot.common.utils.collection.ArrayUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * CompletableFutureUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class CompletableFutureUtils {

    public static CompletableFuture<Void> run( Runnable... runs ) {
        return CompletableFuture.allOf(warpCompletableFuture(true, runs));
    }

    public static void parallel( Runnable... runs ) {
        run(runs).join();
    }

    public static void parallel( Boolean ignoreException, Runnable... runs ) {
        CompletableFuture.allOf(warpCompletableFuture(ignoreException, runs)).join();
    }

    @SafeVarargs
    public static <T> T[] parallel( Supplier<T>... suppliers ) {
        return parallel(true, suppliers);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> T[] parallel( Boolean ignoreException, Supplier<T>... suppliers ) {
        CompletableFuture<T>[] completableFutures =
                warpCompletableFuture(ignoreException, suppliers);
        CompletableFuture.allOf(completableFutures).join();
        T[] objects = (T[]) new Object[completableFutures.length];

        for (int i = 0; i < completableFutures.length; ++i) {
            try {
                objects[i] = completableFutures[i].get();
            } catch (Exception e) {
                if (!ignoreException) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return objects;
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T>[] warpCompletableFuture(
            Boolean ignoreException, Supplier<T>... suppliers ) {
        CompletableFuture<T>[] completableFutures =
                ArrayUtils.createGenericArray(CompletableFuture[]::new, suppliers.length);

        for (int i = 0; i < suppliers.length; ++i) {
            Supplier<T> supplier = suppliers[i];
            completableFutures[i] =
                    CompletableFuture.supplyAsync(
                            () -> {
                                T res;
                                try {
                                    res = supplier.get();
                                    return res;
                                } catch (Exception e) {
                                    if (!ignoreException) {
                                        throw new RuntimeException(e.getMessage());
                                    }

                                    res = null;
                                }
                                return res;
                            });
        }

        return completableFutures;
    }

    @SuppressWarnings("unchecked")
    private static CompletableFuture<Void>[] warpCompletableFuture(
            Boolean ignoreException, Runnable... runs ) {
        CompletableFuture<Void>[] completableFutures =
                ArrayUtils.createGenericArray(CompletableFuture[]::new, runs.length);

        for (int i = 0; i < runs.length; ++i) {
            Runnable runnable = runs[i];
            completableFutures[i] =
                    CompletableFuture.runAsync(
                            () -> {
                                try {
                                    runnable.run();
                                } catch (Exception e) {
                                    if (!ignoreException) {
                                        throw new RuntimeException(e.getMessage());
                                    }
                                }
                            });
        }

        return completableFutures;
    }

    /**
     * 创建并行任务并执行
     *
     * @param list 数据源
     * @param api API调用逻辑
     * @param exceptionHandle 异常处理逻辑
     * @param <S> 数据源类型
     * @param <T> 程序返回类型
     * @return 处理结果列表
     */
    public static <S, T> List<T> parallelFutureJoin(
            Collection<S> list,
            Function<S, T> api,
            Executor executorService,
            BiFunction<Throwable, S, T> exceptionHandle ) {
        // 规整所有任务
        List<CompletableFuture<T>> collectFuture =
                list.stream()
                        .map(
                                s ->
                                        createFuture(
                                                () -> api.apply(s),
                                                executorService,
                                                e ->
                                                        exceptionHandle.apply(
                                                                extractRealException(e), s)))
                        .toList();

        // 汇总所有任务，并执行join，全部执行完成后，统一返回
        return CompletableFuture.allOf(collectFuture.toArray(new CompletableFuture<?>[]{}))
                .thenApply(
                        f ->
                                collectFuture.stream()
                                        .map(CompletableFuture::join)
                                        .filter(Objects::nonNull)
                                        .toList())
                .join();
    }

    /**
     * 创建CompletableFuture任务
     *
     * @param logic 任务逻辑
     * @param exceptionHandle 异常处理
     * @param <T> 类型
     * @return 任务
     */
    public static <T> CompletableFuture<T> createFuture(
            Supplier<T> logic, Executor executorService, Function<Throwable, T> exceptionHandle ) {
        return CompletableFuture.supplyAsync(logic, executorService).exceptionally(exceptionHandle);
    }

    /**
     * 提取真正的异常
     *
     * <p>
     * CompletableFuture抛出的往往不是真正的异常
     *
     * @param throwable 异常
     * @return 真正的异常
     */
    public static Throwable extractRealException( Throwable throwable ) {
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }

    public static <T> CompletableFuture<T> retryOnCondition(
            Supplier<CompletableFuture<T>> supplier,
            Predicate<Throwable> retryPredicate,
            int maxAttempts ) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts can't be <= 0");
        }
        return retryOnCondition(supplier, retryPredicate, null, maxAttempts);
    }

    private static <T> CompletableFuture<T> retryOnCondition(
            Supplier<CompletableFuture<T>> supplier,
            Predicate<Throwable> retryPredicate,
            Throwable lastError,
            int attemptsLeft ) {

        if (attemptsLeft == 0) {
            return CompletableFuture.failedFuture(lastError);
        }

        return supplier.get()
                .thenApply(CompletableFuture::completedFuture)
                .exceptionally(
                        error -> {
                            boolean doRetry = retryPredicate.test(error);
                            int attempts = doRetry ? attemptsLeft - 1 : 0;
                            return retryOnCondition(supplier, retryPredicate, error, attempts);
                        })
                .thenCompose(Function.identity());
    }
}
