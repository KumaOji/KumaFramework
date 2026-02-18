/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

/**
 * 强制toMap收集器 直接使用默认的Collectors.toMap会出现两个问题 1、key重复时抛异常 2、value为空时抛异常 因此封装了这个工具解决这两个问题
 *
 * 使用方式： <pre>
 *     int[] arr = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
 *     Map<Integer, Boolean> map = Arrays.stream(arr).boxed().collect(ForceToMapCollector.collect(Functional.identity(), x -> x % 2 == 0));
 *     LogUtils.info(map);
 * </pre>
 *
 * @param <T> stream中元素的类型
 * @param <K> key的类型
 * @param <V> value的类型
 */
public final class ForceToMapCollector<T, K, V> implements Collector<T, Map<K, V>, Map<K, V>> {

    private Function<? super T, ? extends K> keyMapper;

    private Function<? super T, ? extends V> valueMapper;

    public ForceToMapCollector(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper) {
        super();
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
    }

    /**
     * 创建ForceToMap收集器实例
     * @param keyMapper key的映射器
     * @param valueMapper value的映射器
     * @param <T> stream中元素的类型
     * @param <K> key的类型
     * @param <V> value的类型
     * @return ForceToMapCollector收集器
     */
    public static <T, K, V> ForceToMapCollector<T, K, V> collect(
            Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return new ForceToMapCollector<>(keyMapper, valueMapper);
    }

    /**
     * 创建ForceToMap收集器实例 收集后map的value类型为流的类型
     * @param keyMapper key的映射器
     * @param <T> stream中元素的类型
     * @param <K> key的类型
     * @return ForceToMapCollector收集器
     */
    public static <T, K> ForceToMapCollector<T, K, T> collect(Function<T, K> keyMapper) {
        return new ForceToMapCollector<>(keyMapper, Function.identity());
    }

    @Override
    public BiConsumer<Map<K, V>, T> accumulator() {
        return (map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element));
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
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
}
