/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.kuma.boot.common.utils.lambda;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class RandomKCollector<T>
implements Collector<T, PriorityQueue<Map.Entry<Double, T>>, List<T>> {
    private int k;

    private RandomKCollector(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("K must be positive");
        }
        this.k = k;
    }

    public static <T> RandomKCollector<T> collect(int k) {
        return new RandomKCollector<T>(k);
    }

    @Override
    public Supplier<PriorityQueue<Map.Entry<Double, T>>> supplier() {
        return () -> new PriorityQueue(this.k, Map.Entry.comparingByKey());
    }

    @Override
    public BiConsumer<PriorityQueue<Map.Entry<Double, T>>, T> accumulator() {
        return (queue, t) -> {
            queue.offer(new AbstractMap.SimpleImmutableEntry<Double, Object>(Math.random(), t));
            if (queue.size() > this.k) {
                queue.poll();
            }
        };
    }

    @Override
    public BinaryOperator<PriorityQueue<Map.Entry<Double, T>>> combiner() {
        return (q1, q2) -> {
            q1.addAll(q2);
            return q1;
        };
    }

    @Override
    public Function<PriorityQueue<Map.Entry<Double, T>>, List<T>> finisher() {
        return queue -> IntStream.iterate(0, x -> x + 1).limit(Math.min(this.k, queue.size())).mapToObj(x -> (Map.Entry)queue.poll()).filter(Objects::nonNull).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
    }
}

