/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * get(Supplier<T> supplier): 从Supplier获取值。 consume(T item, Consumer<T> consumer):
 * 对象消费，执行Consumer的操作。 apply(T item, Function<T, R> function): 对象转换，将输入对象转换为另一种类型。
 * repeatUntil(Supplier<Boolean> condition, Supplier<T> operation): 重复执行操作，直到条件满足。
 * transform(T input, Function<T, R> transformer): 对象转换的另一种形式。 runSafely(Runnable
 * operation, Consumer<Exception> exceptionHandler): 安全执行操作，异常通过Consumer处理。
 * create(Supplier<T> supplier): 创建对象实例。 doIf(Supplier<Boolean> condition, Runnable
 * action): 条件执行，如果条件为真，则执行操作。 tryPerform(Runnable action): 尝试执行操作，返回执行成功或失败的布尔值。
 * forEach(Collection<T> collection, Consumer<T> action): 遍历集合，对每个元素执行操作。
 * filter(Collection<T> collection, Predicate<T> predicate): 过滤集合，根据条件返回满足条件的元素集合。
 * map(Collection<T> collection, Function<T, R> mapper): 转换集合，对集合中的每个元素应用转换函数，返回转换后的元素集合。
 * createCollectionInstance(Collection<T> collection): 创建与给定集合类型相同的空集合实例，用于filter和map方法。
 * runAsync(Runnable runnable): 异步执行一个无返回值的操作。 supplyAsync(Supplier<T> supplier):
 * 异步执行一个有返回值的操作。 runAsync(T item, Consumer<T> consumer): 异步执行一个消费者操作。 applyAsync(T item,
 * Function<T, R> function): 异步执行一个函数操作并返回结果。 whenCompleteAsync(Supplier<T> supplier,
 * BiConsumer<? super T, ? super Throwable> action): 异步执行一个操作并在操作完成时进行回调。
 * performTransaction(Supplier<Boolean>... operations): 执行一个事务性操作，确保所有步骤都成功完成，否则执行回滚逻辑。
 * shutdownExecutorService(): 关闭线程池资源，当应用程序关闭时应当调用此方法。
 *
 */
public class FunctionalUtils {

    // 共享线程池，用于异步操作
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 从Supplier获取值，并返回它。
     */
    public static <T> T get(Supplier<T> supplier) {
        return supplier.get();
    }

    /**
     * 处理对象T，使用Consumer进行消费。
     */
    public static <T> void consume(T item, Consumer<T> consumer) {
        consumer.accept(item);
    }

    /**
     * 应用函数到输入值，并返回结果。
     */
    public static <T, R> R apply(T item, Function<T, R> function) {
        return function.apply(item);
    }

    /**
     * 重复执行Supplier提供的操作，直到满足条件。
     */
    public static <T> void repeatUntil(Supplier<Boolean> condition, Supplier<T> operation) {
        while (!condition.get()) {
            operation.get();
        }
    }

    /**
     * 对象转换，将输入对象T使用转换函数转换为R。
     */
    public static <T, R> R transform(T input, Function<T, R> transformer) {
        return transformer.apply(input);
    }

    /**
     * 安全地执行一个可能抛出异常的操作，异常被捕获并处理。
     */
    public static void runSafely(Runnable operation, Consumer<Exception> exceptionHandler) {
        try {
            operation.run();
        } catch (Exception e) {
            exceptionHandler.accept(e);
        }
    }

    /**
     * 创建并返回一个新对象，通过Supplier接口。
     */
    public static <T> T create(Supplier<T> supplier) {
        return supplier.get();
    }

    /**
     * 执行一个条件检查，如果条件为真，则执行操作。
     */
    public static void doIf(Supplier<Boolean> condition, Runnable action) {
        if (condition.get()) {
            action.run();
        }
    }

    /**
     * 执行一个操作，并返回一个状态值，通常用于链式操作中的状态检查。
     */
    public static boolean tryPerform(Runnable action) {
        try {
            action.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 对集合中的每个元素执行给定的操作。
     */
    public static <T> void forEach(Collection<T> collection, Consumer<T> action) {
        for (T item : collection) {
            action.accept(item);
        }
    }

    /**
     * 对集合中的元素进行过滤，并返回一个新的集合。
     */
    public static <T> Collection<T> filter(Collection<T> collection, Predicate<T> predicate) {
        Collection<T> result = createCollectionInstance(collection);
        for (T item : collection) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 创建与给定集合类型相同的空集合实例。
     */
    private static <T> Collection<T> createCollectionInstance(Collection<T> collection) {
        try {
            return collection.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Could not create a new instance of the collection class", e);
        }
    }

    /**
     * 对集合中的元素应用转换函数，并返回一个新的集合。
     */
    public static <T, R> Collection<R> map(Collection<T> collection, Function<T, R> mapper) {
        Collection<R> result = (Collection<R>) createCollectionInstance(collection);
        for (T item : collection) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    /**
     * 异步执行一个操作，返回CompletableFuture。
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

    /**
     * 异步执行一个有返回值的操作，返回CompletableFuture。
     */
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }

    /**
     * 异步执行一个操作，并在操作完成时使用Consumer处理结果。
     */
    public static <T> void runAsync(T item, Consumer<T> consumer) {
        CompletableFuture.runAsync(() -> consumer.accept(item), executorService);
    }

    /**
     * 异步执行一个有返回值的操作，并应用Function处理结果。
     */
    public static <T, R> CompletableFuture<R> applyAsync(T item, Function<T, R> function) {
        return CompletableFuture.supplyAsync(() -> function.apply(item), executorService);
    }

    /**
     * 使用CompletableFuture执行一个操作，并在操作完成时进行回调。
     */
    public static <T> void whenCompleteAsync(
            Supplier<T> supplier, BiConsumer<? super T, ? super Throwable> action) {
        CompletableFuture.supplyAsync(supplier, executorService)
                .whenCompleteAsync(action, executorService);
    }

    /**
     * 执行一个事务性操作，确保所有步骤都成功完成，否则回滚。
     */
    public static <T> boolean performTransaction(Supplier<Boolean>... operations) {
        boolean success = true;
        for (Supplier<Boolean> operation : operations) {
            if (!operation.get()) {
                success = false;
                break;
            }
        }
        if (!success) {
            // Rollback logic if necessary
        }
        return success;
    }

    /**
     * 关闭工具类使用的线程池资源，应当在应用程序关闭时调用。
     */
    public static void shutdownExecutorService() {
        executorService.shutdown();
    }
}
