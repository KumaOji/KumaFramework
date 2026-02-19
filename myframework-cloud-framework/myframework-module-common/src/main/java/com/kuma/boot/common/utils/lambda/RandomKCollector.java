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

/**
 * 实现RandomK的collector 使用方式： <pre>
 *     int[] arr = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
 *     List<Integer> list = Arrays.stream(arr).boxed().collect(RandomKCollector.collect(6));
 *     LogUtils.info(list);
 * </pre>
 *
 * @param <T> stream中元素的类型
 */
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

    /**
     * 创建RandomK收集器实例
     * @param k 需要多少个元素
     * @param <T> 元素类型
     * @return RandomK收集器
     */
    public static <T> RandomKCollector<T> collect(int k) {
        return new RandomKCollector<>(k);
    }

    @Override
    public Supplier<PriorityQueue<Map.Entry<Double, T>>> supplier() {
        return () -> new PriorityQueue<>(k, Map.Entry.<Double, T>comparingByKey());
    }

    @Override
    public BiConsumer<PriorityQueue<Map.Entry<Double, T>>, T> accumulator() {
        return (queue, t) -> {
            queue.offer(new AbstractMap.SimpleImmutableEntry<>(Math.random(), t));
            if (queue.size() > k) {
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
        return queue ->
                IntStream.iterate(0, x -> x + 1)
                        .limit(Math.min(k, queue.size()))
                        .mapToObj(x -> queue.poll())
                        .filter(Objects::nonNull)
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
    }
}
