/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import com.kuma.boot.common.support.dataframe.iframe.function.BigDecimalFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.NumberFunction;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CollectorsPlusUtil {
    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    private CollectorsPlusUtil() {
    }

    private static <I, R> Function<I, R> castingIdentity() {
        return i -> i;
    }

    public static <T, R extends Number> Collector<T, ?, BigDecimal> summingBigDecimalForNumber(NumberFunction<T, R> numberFunction) {
        BigDecimalFunction<T> mapper = CollectorsPlusUtil.getBigDecimalFunction(numberFunction);
        return CollectorsPlusUtil.summingBigDecimal(mapper);
    }

    private static <T, R extends Number> BigDecimalFunction<? super T> getBigDecimalFunction(NumberFunction<T, R> numberFunction) {
        return e -> {
            Object apply = numberFunction.apply(e);
            if (apply == null) {
                return null;
            }
            return apply instanceof BigDecimal ? (BigDecimal)apply : new BigDecimal(apply.toString());
        };
    }

    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(BigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[1], (a, t) -> {
            if (a[0] == null) {
                a[0] = BigDecimal.ZERO;
            }
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    public static List<Map.Entry<String, BigDecimal>> sortByValueAndReverse(List<Map.Entry<String, BigDecimal>> unorderedList) {
        Collections.sort(unorderedList, new Comparator<Map.Entry<String, BigDecimal>>(){

            @Override
            public int compare(Map.Entry<String, BigDecimal> o1, Map.Entry<String, BigDecimal> o2) {
                BigDecimal p = o2.getValue().subtract(o1.getValue());
                if (p.compareTo(BigDecimal.ZERO) > 0) {
                    return 1;
                }
                if (p.compareTo(BigDecimal.ZERO) == 0) {
                    return 0;
                }
                return -1;
            }
        });
        return unorderedList;
    }

    public static List<Map.Entry<String, Long>> sortByValueAndReverseForLong(List<Map.Entry<String, Long>> unorderedList) {
        Collections.sort(unorderedList, new Comparator<Map.Entry<String, Long>>(){

            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                Long p = o2.getValue() - o1.getValue();
                if (p > 0L) {
                    return 1;
                }
                if (p == 0L) {
                    return 0;
                }
                return -1;
            }
        });
        return unorderedList;
    }

    public static <T> Collector<T, ?, BigDecimal> maxBy(BigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{new BigDecimal(Integer.MIN_VALUE)}, (a, t) -> {
            a[0] = a[0].max(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].max(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    public static <T> Collector<T, ?, BigDecimal> minBy(BigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{new BigDecimal(Integer.MAX_VALUE)}, (a, t) -> {
            a[0] = a[0].min(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].min(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    public static <T, R extends Number> Collector<T, ?, BigDecimal> averagingBigDecimal(NumberFunction<T, R> mapper, int newScale, int roundingMode) {
        return CollectorsPlusUtil.averagingBigDecimal(CollectorsPlusUtil.getBigDecimalFunction(mapper), newScale, roundingMode);
    }

    public static <T> Collector<T, ?, BigDecimal> averagingBigDecimal(BigDecimalFunction<? super T> mapper, int newScale, int roundingMode) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{new BigDecimal(0), new BigDecimal(0)}, (a, t) -> {
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
            a[1] = a[1].add(BigDecimal.ONE);
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0].divide(a[1], MathContext.DECIMAL32).setScale(newScale, roundingMode), CH_NOID);
    }

    static class CollectorImpl<T, A, R>
    implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Collector.Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher, Set<Collector.Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Collector.Characteristics> characteristics) {
            this(supplier, accumulator, combiner, CollectorsPlusUtil.castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return this.accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return this.supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return this.combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return this.finisher;
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return this.characteristics;
        }
    }
}

