/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FunctionalUtils {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static <T> T get(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> void consume(T item, Consumer<T> consumer) {
        consumer.accept(item);
    }

    public static <T, R> R apply(T item, Function<T, R> function) {
        return function.apply(item);
    }

    public static <T> void repeatUntil(Supplier<Boolean> condition, Supplier<T> operation) {
        while (!condition.get().booleanValue()) {
            operation.get();
        }
    }

    public static <T, R> R transform(T input, Function<T, R> transformer) {
        return transformer.apply(input);
    }

    public static void runSafely(Runnable operation, Consumer<Exception> exceptionHandler) {
        try {
            operation.run();
        }
        catch (Exception e) {
            exceptionHandler.accept(e);
        }
    }

    public static <T> T create(Supplier<T> supplier) {
        return supplier.get();
    }

    public static void doIf(Supplier<Boolean> condition, Runnable action) {
        if (condition.get().booleanValue()) {
            action.run();
        }
    }

    public static boolean tryPerform(Runnable action) {
        try {
            action.run();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static <T> void forEach(Collection<T> collection, Consumer<T> action) {
        for (T item : collection) {
            action.accept(item);
        }
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<T> predicate) {
        Collection<T> result = FunctionalUtils.createCollectionInstance(collection);
        for (T item : collection) {
            if (!predicate.test(item)) continue;
            result.add(item);
        }
        return result;
    }

    private static <T> Collection<T> createCollectionInstance(Collection<T> collection) {
        try {
            return (Collection)collection.getClass().newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Could not create a new instance of the collection class", e);
        }
    }

    public static <T, R> Collection<R> map(Collection<T> collection, Function<T, R> mapper) {
        Collection<T> result = FunctionalUtils.createCollectionInstance(collection);
        for (T item : collection) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }

    public static <T> void runAsync(T item, Consumer<T> consumer) {
        CompletableFuture.runAsync(() -> consumer.accept(item), executorService);
    }

    public static <T, R> CompletableFuture<R> applyAsync(T item, Function<T, R> function) {
        return CompletableFuture.supplyAsync(() -> function.apply(item), executorService);
    }

    public static <T> void whenCompleteAsync(Supplier<T> supplier, BiConsumer<? super T, ? super Throwable> action) {
        CompletableFuture.supplyAsync(supplier, executorService).whenCompleteAsync(action, (Executor)executorService);
    }

    public static <T> boolean performTransaction(Supplier<Boolean> ... operations) {
        boolean success = true;
        for (Supplier<Boolean> operation : operations) {
            if (operation.get().booleanValue()) continue;
            success = false;
            break;
        }
        if (!success) {
            // empty if block
        }
        return success;
    }

    public static void shutdownExecutorService() {
        executorService.shutdown();
    }
}

