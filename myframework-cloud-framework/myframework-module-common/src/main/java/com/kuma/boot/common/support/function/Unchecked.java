/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

import com.kuma.boot.common.support.function.CheckedCallable;
import com.kuma.boot.common.support.function.CheckedComparator;
import com.kuma.boot.common.support.function.CheckedConsumer;
import com.kuma.boot.common.support.function.CheckedFunction;
import com.kuma.boot.common.support.function.CheckedPredicate;
import com.kuma.boot.common.support.function.CheckedRunnable;
import com.kuma.boot.common.support.function.CheckedSupplier;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Unchecked {
    public static <T, R> Function<T, R> function(CheckedFunction<T, R> function) {
        Objects.requireNonNull(function);
        return t -> {
            try {
                return function.apply(t);
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    public static <T> Consumer<T> consumer(CheckedConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        return t -> {
            try {
                consumer.accept(t);
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    public static <T> Supplier<T> supplier(CheckedSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return () -> {
            try {
                return supplier.get();
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    public static <T> Predicate<T> predicate(CheckedPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return t -> {
            try {
                return predicate.test(t);
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    public static Runnable runnable(CheckedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return () -> {
            try {
                runnable.run();
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    public static <T> Callable<T> callable(CheckedCallable<T> callable) {
        Objects.requireNonNull(callable);
        return () -> {
            try {
                return callable.call();
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    public static <T> Comparator<T> comparator(CheckedComparator<T> comparator) {
        Objects.requireNonNull(comparator);
        return (o1, o2) -> {
            try {
                return comparator.compare(o1, o2);
            }
            catch (Throwable e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }
}

