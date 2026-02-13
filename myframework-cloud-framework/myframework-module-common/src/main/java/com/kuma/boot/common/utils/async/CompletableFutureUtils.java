/*
 * Decompiled with CFR 0.152.
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

public class CompletableFutureUtils {
    public static CompletableFuture<Void> run(Runnable ... runs) {
        return CompletableFuture.allOf(CompletableFutureUtils.warpCompletableFuture((Boolean)true, runs));
    }

    public static void parallel(Runnable ... runs) {
        CompletableFutureUtils.run(runs).join();
    }

    public static void parallel(Boolean ignoreException, Runnable ... runs) {
        CompletableFuture.allOf(CompletableFutureUtils.warpCompletableFuture(ignoreException, runs)).join();
    }

    @SafeVarargs
    public static <T> T[] parallel(Supplier<T> ... suppliers) {
        return CompletableFutureUtils.parallel((Boolean)true, suppliers);
    }

    @SafeVarargs
    public static <T> T[] parallel(Boolean ignoreException, Supplier<T> ... suppliers) {
        CompletableFuture<T>[] completableFutures = CompletableFutureUtils.warpCompletableFuture(ignoreException, suppliers);
        CompletableFuture.allOf(completableFutures).join();
        Object[] objects = new Object[completableFutures.length];
        for (int i = 0; i < completableFutures.length; ++i) {
            try {
                objects[i] = completableFutures[i].get();
                continue;
            }
            catch (Exception e) {
                if (ignoreException.booleanValue()) continue;
                throw new RuntimeException(e.getMessage());
            }
        }
        return objects;
    }

    private static <T> CompletableFuture<T>[] warpCompletableFuture(Boolean ignoreException, Supplier<T> ... suppliers) {
        CompletableFuture[] completableFutures = (CompletableFuture[])ArrayUtils.createGenericArray(CompletableFuture[]::new, suppliers.length);
        for (int i = 0; i < suppliers.length; ++i) {
            Supplier supplier = suppliers[i];
            completableFutures[i] = CompletableFuture.supplyAsync(() -> {
                try {
                    Object res = supplier.get();
                    return res;
                }
                catch (Exception e) {
                    if (!ignoreException.booleanValue()) {
                        throw new RuntimeException(e.getMessage());
                    }
                    Object res = null;
                    return res;
                }
            });
        }
        return completableFutures;
    }

    private static CompletableFuture<Void>[] warpCompletableFuture(Boolean ignoreException, Runnable ... runs) {
        CompletableFuture[] completableFutures = (CompletableFuture[])ArrayUtils.createGenericArray(CompletableFuture[]::new, runs.length);
        for (int i = 0; i < runs.length; ++i) {
            Runnable runnable = runs[i];
            completableFutures[i] = CompletableFuture.runAsync(() -> {
                block2: {
                    try {
                        runnable.run();
                    }
                    catch (Exception e) {
                        if (ignoreException.booleanValue()) break block2;
                        throw new RuntimeException(e.getMessage());
                    }
                }
            });
        }
        return completableFutures;
    }

    public static <S, T> List<T> parallelFutureJoin(Collection<S> list, Function<S, T> api, Executor executorService, BiFunction<Throwable, S, T> exceptionHandle) {
        List<CompletableFuture> collectFuture = list.stream().map(s -> CompletableFutureUtils.createFuture(() -> api.apply(s), executorService, e -> exceptionHandle.apply(CompletableFutureUtils.extractRealException(e), s))).toList();
        return (List)((CompletableFuture)CompletableFuture.allOf(collectFuture.toArray(new CompletableFuture[0])).thenApply(f -> collectFuture.stream().map(CompletableFuture::join).filter(Objects::nonNull).toList())).join();
    }

    public static <T> CompletableFuture<T> createFuture(Supplier<T> logic, Executor executorService, Function<Throwable, T> exceptionHandle) {
        return CompletableFuture.supplyAsync(logic, executorService).exceptionally((Function)exceptionHandle);
    }

    public static Throwable extractRealException(Throwable throwable) {
        if ((throwable instanceof CompletionException || throwable instanceof ExecutionException) && throwable.getCause() != null) {
            return throwable.getCause();
        }
        return throwable;
    }

    public static <T> CompletableFuture<T> retryOnCondition(Supplier<CompletableFuture<T>> supplier, Predicate<Throwable> retryPredicate, int maxAttempts) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts can't be <= 0");
        }
        return CompletableFutureUtils.retryOnCondition(supplier, retryPredicate, null, maxAttempts);
    }

    private static <T> CompletableFuture<T> retryOnCondition(Supplier<CompletableFuture<T>> supplier, Predicate<Throwable> retryPredicate, Throwable lastError, int attemptsLeft) {
        if (attemptsLeft == 0) {
            return CompletableFuture.failedFuture(lastError);
        }
        return ((CompletableFuture)((CompletableFuture)supplier.get().thenApply(CompletableFuture::completedFuture)).exceptionally(error -> {
            boolean doRetry = retryPredicate.test((Throwable)error);
            int attempts = doRetry ? attemptsLeft - 1 : 0;
            return CompletableFutureUtils.retryOnCondition(supplier, retryPredicate, error, attempts);
        })).thenCompose(Function.identity());
    }
}

