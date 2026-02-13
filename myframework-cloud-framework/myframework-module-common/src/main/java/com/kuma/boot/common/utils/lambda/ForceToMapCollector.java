/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lambda;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ForceToMapCollector<T, K, V>
implements Collector<T, Map<K, V>, Map<K, V>> {
    private Function<? super T, ? extends K> keyMapper;
    private Function<? super T, ? extends V> valueMapper;

    public ForceToMapCollector(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
    }

    public static <T, K, V> ForceToMapCollector<T, K, V> collect(Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return new ForceToMapCollector<T, K, V>(keyMapper, valueMapper);
    }

    public static <T, K> ForceToMapCollector<T, K, T> collect(Function<T, K> keyMapper) {
        return new ForceToMapCollector(keyMapper, Function.identity());
    }

    @Override
    public BiConsumer<Map<K, V>, T> accumulator() {
        return (map, element) -> map.put(this.keyMapper.apply(element), this.valueMapper.apply(element));
    }

    @Override
    public Supplier<Map<K, V>> supplier() {
        return HashMap::new;
    }

    @Override
    public BinaryOperator<Map<K, V>> combiner() {
        return (x, y) -> {
            x.putAll(y);
            return x;
        };
    }

    @Override
    public Function<Map<K, V>, Map<K, V>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    }
}

