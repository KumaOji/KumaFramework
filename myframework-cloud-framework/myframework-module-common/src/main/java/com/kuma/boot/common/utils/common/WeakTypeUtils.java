/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class WeakTypeUtils {
    private WeakTypeUtils() {
    }

    public static boolean toBoolean(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        if (obj instanceof Number) {
            return ((Number)obj).doubleValue() != 0.0;
        }
        if (obj instanceof CharSequence) {
            return !((CharSequence)obj).isEmpty();
        }
        return true;
    }

    public static <T> T or(T obj1, T obj2, T ... objs) {
        Stream<Object> stream = Stream.of(obj1, obj2);
        if (objs != null && objs.length > 0) {
            stream = Stream.concat(stream, Arrays.stream(objs));
        }
        return (T)WeakTypeUtils.or(stream);
    }

    public static <T> T or(Supplier<T> supplier1, Supplier<T> supplier2, Supplier<T> ... suppliers) {
        Stream<Supplier> stream = Stream.of(supplier1, supplier2);
        if (suppliers != null && suppliers.length > 0) {
            stream = Stream.concat(stream, Arrays.stream(suppliers));
        }
        return (T)WeakTypeUtils.or(stream.map(Supplier::get));
    }

    private static <T> T or(Stream<T> stream) {
        T last = null;
        Iterator iterator = stream.iterator();
        while (iterator.hasNext() && !WeakTypeUtils.toBoolean(last = (T)iterator.next())) {
        }
        return last;
    }

    public static <T> T and(T obj1, T obj2, T ... objs) {
        Stream<Object> stream = Stream.of(obj1, obj2);
        if (objs != null && objs.length > 0) {
            stream = Stream.concat(stream, Arrays.stream(objs));
        }
        return (T)WeakTypeUtils.and(stream);
    }

    public static <T> T and(Supplier<T> supplier1, Supplier<T> supplier2, Supplier<T> ... suppliers) {
        Stream<Supplier> stream = Stream.of(supplier1, supplier2);
        if (suppliers != null && suppliers.length > 0) {
            stream = Stream.concat(stream, Arrays.stream(suppliers));
        }
        return (T)WeakTypeUtils.and(stream.map(Supplier::get));
    }

    private static <T> T and(Stream<T> stream) {
        T last = null;
        Iterator iterator = stream.iterator();
        while (iterator.hasNext() && WeakTypeUtils.toBoolean(last = (T)iterator.next())) {
        }
        return last;
    }

    public static <T> void ifThen(Supplier<T> supplier, Predicate<T> predicate, Consumer<T> consumer) {
        T t = supplier.get();
        if (predicate.test(t)) {
            consumer.accept(t);
        }
    }

    public static <T> void ifThen(T t, Predicate<T> predicate, Runnable runnable) {
        WeakTypeUtils.ifThen(predicate.test(t), runnable);
    }

    public static void ifThen(Supplier<Boolean> supplier, Runnable runnable) {
        WeakTypeUtils.ifThen(supplier.get(), runnable);
    }

    public static <T> void ifThen(Object object, Runnable runnable) {
        WeakTypeUtils.ifThen(WeakTypeUtils.toBoolean(object), runnable);
    }

    public static <T> void ifThen(boolean b, Runnable runnable) {
        if (b) {
            runnable.run();
        }
    }

    public static <T> void ifElse(Supplier<T> supplier, Predicate<T> predicate, Consumer<T> tConsumer, Consumer<T> fConsumer) {
        T t = supplier.get();
        if (predicate.test(t)) {
            tConsumer.accept(t);
        } else {
            fConsumer.accept(t);
        }
    }

    public static <T> void ifElse(T t, Predicate<T> predicate, Runnable tRunnable, Runnable fRunnable) {
        WeakTypeUtils.ifElse(predicate.test(t), tRunnable, fRunnable);
    }

    public static void ifElse(Supplier<Boolean> supplier, Runnable tRunnable, Runnable fRunnable) {
        WeakTypeUtils.ifElse(supplier.get(), tRunnable, fRunnable);
    }

    public static <T> void ifElse(Object object, Runnable tRunnable, Runnable fRunnable) {
        WeakTypeUtils.ifElse(WeakTypeUtils.toBoolean(object), tRunnable, fRunnable);
    }

    public static <T> void ifElse(boolean b, Runnable tRunnable, Runnable fRunnable) {
        if (b) {
            tRunnable.run();
        } else {
            fRunnable.run();
        }
    }
}

