/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaBuilder<T> {
    private final Supplier<T> constructor;
    private final List<Consumer<T>> dInjects = new ArrayList<Consumer<T>>();
    private Consumer<T> head = t -> {};

    private LambdaBuilder(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public static <T> LambdaBuilder<T> builder(Supplier<T> constructor) {
        return new LambdaBuilder<T>(constructor);
    }

    public <P1> LambdaBuilder<T> with(DInjectConsumer<T, P1> consumer, P1 p1) {
        Consumer<Object> c = instance -> consumer.accept(instance, p1);
        this.head = this.head.andThen(c);
        return this;
    }

    public <P1> LambdaBuilder<T> with(DInjectConsumer<T, P1> consumer, P1 p1, Predicate<P1> predicate) {
        if (null != predicate && !predicate.test(p1)) {
            throw new RuntimeException(String.format("\u3010%s\u3011\u53c2\u6570\u4e0d\u7b26\u5408\u901a\u7528\u4e1a\u52a1\u89c4\u5219\uff01", p1));
        }
        Consumer<Object> c = instance -> consumer.accept(instance, p1);
        this.head = this.head.andThen(c);
        return this;
    }

    public <P1, P2> LambdaBuilder<T> with(DInjectConsumer2<T, P1, P2> consumer, P1 p1, P2 p2) {
        Consumer<Object> c = instance -> consumer.accept(instance, p1, p2);
        this.head = this.head.andThen(c);
        return this;
    }

    public T build() {
        T instance = this.constructor.get();
        this.head.accept(instance);
        return instance;
    }

    @FunctionalInterface
    public static interface DInjectConsumer<T, P1> {
        public void accept(T var1, P1 var2);
    }

    @FunctionalInterface
    public static interface DInjectConsumer2<T, P1, P2> {
        public void accept(T var1, P1 var2, P2 var3);
    }
}

