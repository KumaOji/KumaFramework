/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.map.MapUtil
 */
package com.kuma.boot.common.utils.lambda;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.kuma.boot.common.utils.lambda.EnumerationSpliterator;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {
    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return iterable instanceof Collection ? ((Collection)iterable).stream() : StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    public static IntStream stream(OptionalInt optional) {
        return optional.isPresent() ? IntStream.of(optional.getAsInt()) : IntStream.empty();
    }

    public static LongStream stream(OptionalLong optional) {
        return optional.isPresent() ? LongStream.of(optional.getAsLong()) : LongStream.empty();
    }

    public static DoubleStream stream(OptionalDouble optional) {
        return optional.isPresent() ? DoubleStream.of(optional.getAsDouble()) : DoubleStream.empty();
    }

    public static <A, B, R> Stream<R> zip(Stream<A> streamA, Stream<B> streamB, final BiFunction<? super A, ? super B, R> function) {
        boolean isParallel = streamA.isParallel() || streamB.isParallel();
        Spliterator splitA = streamA.spliterator();
        Spliterator splitB = streamB.spliterator();
        int characteristics = splitA.characteristics() & splitB.characteristics() & 0x50;
        final Iterator itrA = Spliterators.iterator(splitA);
        final Iterator itrB = Spliterators.iterator(splitB);
        return (Stream)((Stream)StreamSupport.stream(new Spliterators.AbstractSpliterator<R>(Math.min(splitA.estimateSize(), splitB.estimateSize()), characteristics){

            @Override
            public boolean tryAdvance(Consumer<? super R> action) {
                if (itrA.hasNext() && itrB.hasNext()) {
                    action.accept(function.apply(itrA.next(), itrB.next()));
                    return true;
                }
                return false;
            }
        }, isParallel).onClose(streamA::close)).onClose(streamB::close);
    }

    public static <A, B> void forEachPair(Stream<A> streamA, Stream<B> streamB, BiConsumer<? super A, ? super B> consumer) {
        if (streamA.isParallel() || streamB.isParallel()) {
            StreamUtils.zip(streamA, streamB, TemporaryPair::new).forEach(pair -> consumer.accept(pair.a, pair.b));
        } else {
            Iterator iterA = streamA.iterator();
            Iterator iterB = streamB.iterator();
            while (iterA.hasNext() && iterB.hasNext()) {
                consumer.accept(iterA.next(), iterB.next());
            }
        }
    }

    public static <T, R> Stream<R> mapWithIndex(Stream<T> stream, final FunctionWithIndex<? super T, ? extends R> function) {
        boolean isParallel = stream.isParallel();
        Spliterator fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final Iterator fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream)StreamSupport.stream(new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50){
                long index;
                {
                    super(est, additionalCharacteristics);
                    this.index = 0L;
                }

                @Override
                public boolean tryAdvance(Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.next(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class Splitr
        extends MapWithIndexSpliterator<Spliterator<T>, R, Splitr>
        implements Consumer<T> {
            T holder;
            final /* synthetic */ FunctionWithIndex val$function;

            Splitr(Spliterator<T> splitr, long index) {
                this.val$function = var4_3;
                super(splitr, index);
            }

            @Override
            public void accept(T t) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance(Consumer<? super R> action) {
                if (this.fromSpliterator.tryAdvance(this)) {
                    try {
                        action.accept(this.val$function.apply(StreamUtils.uncheckedCastNullableTToT(this.holder), this.index++));
                        boolean bl = true;
                        return bl;
                    }
                    finally {
                        this.holder = null;
                    }
                }
                return false;
            }

            @Override
            Splitr createSplit(Spliterator<T> from, long i) {
                return new Splitr(from, i, this.val$function);
            }
        }
        return (Stream)StreamSupport.stream(new Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }

    public static <R> Stream<R> mapWithIndex(IntStream stream, final IntFunctionWithIndex<R> function) {
        boolean isParallel = stream.isParallel();
        Spliterator.OfInt fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfInt fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream)StreamSupport.stream(new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50){
                long index;
                {
                    super(est, additionalCharacteristics);
                    this.index = 0L;
                }

                @Override
                public boolean tryAdvance(Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.nextInt(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class Splitr
        extends MapWithIndexSpliterator<Spliterator.OfInt, R, Splitr>
        implements IntConsumer,
        Spliterator<R> {
            int holder;
            final /* synthetic */ IntFunctionWithIndex val$function;

            Splitr(Spliterator.OfInt splitr, long index) {
                this.val$function = var4_3;
                super(splitr, index);
            }

            @Override
            public void accept(int t) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance(Consumer<? super R> action) {
                if (((Spliterator.OfInt)this.fromSpliterator).tryAdvance(this)) {
                    action.accept(this.val$function.apply(this.holder, this.index++));
                    return true;
                }
                return false;
            }

            @Override
            Splitr createSplit(Spliterator.OfInt from, long i) {
                return new Splitr(from, i, this.val$function);
            }
        }
        return (Stream)StreamSupport.stream(new Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }

    public static <R> Stream<R> mapWithIndex(LongStream stream, final LongFunctionWithIndex<R> function) {
        boolean isParallel = stream.isParallel();
        Spliterator.OfLong fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfLong fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream)StreamSupport.stream(new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50){
                long index;
                {
                    super(est, additionalCharacteristics);
                    this.index = 0L;
                }

                @Override
                public boolean tryAdvance(Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.nextLong(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class Splitr
        extends MapWithIndexSpliterator<Spliterator.OfLong, R, Splitr>
        implements LongConsumer,
        Spliterator<R> {
            long holder;
            final /* synthetic */ LongFunctionWithIndex val$function;

            Splitr(Spliterator.OfLong splitr, long index) {
                this.val$function = var4_3;
                super(splitr, index);
            }

            @Override
            public void accept(long t) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance(Consumer<? super R> action) {
                if (((Spliterator.OfLong)this.fromSpliterator).tryAdvance(this)) {
                    action.accept(this.val$function.apply(this.holder, this.index++));
                    return true;
                }
                return false;
            }

            @Override
            Splitr createSplit(Spliterator.OfLong from, long i) {
                return new Splitr(from, i, this.val$function);
            }
        }
        return (Stream)StreamSupport.stream(new Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }

    public static <R> Stream<R> mapWithIndex(DoubleStream stream, final DoubleFunctionWithIndex<R> function) {
        boolean isParallel = stream.isParallel();
        Spliterator.OfDouble fromSpliterator = stream.spliterator();
        if (!fromSpliterator.hasCharacteristics(16384)) {
            final PrimitiveIterator.OfDouble fromIterator = Spliterators.iterator(fromSpliterator);
            return (Stream)StreamSupport.stream(new Spliterators.AbstractSpliterator<R>(fromSpliterator.estimateSize(), fromSpliterator.characteristics() & 0x50){
                long index;
                {
                    super(est, additionalCharacteristics);
                    this.index = 0L;
                }

                @Override
                public boolean tryAdvance(Consumer<? super R> action) {
                    if (fromIterator.hasNext()) {
                        action.accept(function.apply(fromIterator.nextDouble(), this.index++));
                        return true;
                    }
                    return false;
                }
            }, isParallel).onClose(stream::close);
        }
        class Splitr
        extends MapWithIndexSpliterator<Spliterator.OfDouble, R, Splitr>
        implements DoubleConsumer,
        Spliterator<R> {
            double holder;
            final /* synthetic */ DoubleFunctionWithIndex val$function;

            Splitr(Spliterator.OfDouble splitr, long index) {
                this.val$function = var4_3;
                super(splitr, index);
            }

            @Override
            public void accept(double t) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance(Consumer<? super R> action) {
                if (((Spliterator.OfDouble)this.fromSpliterator).tryAdvance(this)) {
                    action.accept(this.val$function.apply(this.holder, this.index++));
                    return true;
                }
                return false;
            }

            @Override
            Splitr createSplit(Spliterator.OfDouble from, long i) {
                return new Splitr(from, i, this.val$function);
            }
        }
        return (Stream)StreamSupport.stream(new Splitr(fromSpliterator, 0L, function), isParallel).onClose(stream::close);
    }

    public static <T> Optional<T> findLast(Stream<T> stream) {
        class OptionalState {
            boolean set = false;
            T value = null;

            OptionalState() {
            }

            void set(T value) {
                this.set = true;
                this.value = value;
            }

            T get() {
                return Objects.requireNonNull(this.value);
            }
        }
        OptionalState state = new OptionalState();
        ArrayDeque splits = new ArrayDeque();
        splits.addLast(stream.spliterator());
        while (!splits.isEmpty()) {
            Spliterator<Object> prefix;
            Spliterator<Object> spliterator;
            block7: {
                block6: {
                    spliterator = (Spliterator<Object>)splits.removeLast();
                    if (spliterator.getExactSizeIfKnown() == 0L) continue;
                    if (spliterator.hasCharacteristics(16384)) {
                        while ((prefix = spliterator.trySplit()) != null && prefix.getExactSizeIfKnown() != 0L) {
                            if (spliterator.getExactSizeIfKnown() != 0L) continue;
                            spliterator = prefix;
                            break;
                        }
                        spliterator.forEachRemaining(state::set);
                        return Optional.of(state.get());
                    }
                    prefix = spliterator.trySplit();
                    if (prefix == null) break block6;
                    if (prefix.getExactSizeIfKnown() != 0L) break block7;
                }
                spliterator.forEachRemaining(state::set);
                if (!state.set) continue;
                return Optional.of(state.get());
            }
            splits.addLast(prefix);
            splits.addLast(spliterator);
        }
        return Optional.empty();
    }

    public static OptionalInt findLast(IntStream stream) {
        Optional<Integer> boxedLast = StreamUtils.findLast(stream.boxed());
        return boxedLast.map(OptionalInt::of).orElseGet(OptionalInt::empty);
    }

    public static OptionalLong findLast(LongStream stream) {
        Optional<Long> boxedLast = StreamUtils.findLast(stream.boxed());
        return boxedLast.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    }

    public static OptionalDouble findLast(DoubleStream stream) {
        Optional<Double> boxedLast = StreamUtils.findLast(stream.boxed());
        return boxedLast.map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
    }

    public static <T> T uncheckedCastNullableTToT(T t) {
        return t;
    }

    public static <E> List<E> filter(Collection<E> collection, Predicate<E> function) {
        if (CollUtil.isEmpty(collection)) {
            return new ArrayList();
        }
        return collection.stream().filter(function).toList();
    }

    public static <E> String join(Collection<E> collection, Function<E, String> function) {
        return StreamUtils.join(collection, function, ",");
    }

    public static <E> String join(Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return "";
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }

    public static <E> List<E> sorted(Collection<E> collection, Comparator<E> comparing) {
        if (CollUtil.isEmpty(collection)) {
            return new ArrayList();
        }
        return collection.stream().sorted(comparing).toList();
    }

    public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().collect(Collectors.toMap(key, Function.identity(), (l, r) -> l));
    }

    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().collect(Collectors.toMap(key, value, (l, r) -> l));
    }

    public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().collect(Collectors.groupingBy(key, LinkedHashMap::new, Collectors.toList()));
    }

    public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(Collection<E> collection, Function<E, K> key1, Function<E, U> key2) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().collect(Collectors.groupingBy(key1, LinkedHashMap::new, Collectors.groupingBy(key2, LinkedHashMap::new, Collectors.toList())));
    }

    public static <E, T, U> Map<T, Map<U, E>> group2Map(Collection<E> collection, Function<E, T> key1, Function<E, U> key2) {
        if (CollUtil.isEmpty(collection) || key1 == null || key2 == null) {
            return MapUtil.newHashMap();
        }
        return collection.stream().collect(Collectors.groupingBy(key1, LinkedHashMap::new, Collectors.toMap(key2, Function.identity(), (l, r) -> l)));
    }

    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection)) {
            return new ArrayList();
        }
        return collection.stream().map(function).filter(Objects::nonNull).toList();
    }

    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection) || function == null) {
            return new HashSet();
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <K, X, Y, V> Map<K, V> merge(Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
        if (MapUtil.isEmpty(map1) && MapUtil.isEmpty(map2)) {
            return MapUtil.newHashMap();
        }
        if (MapUtil.isEmpty(map1)) {
            map1 = MapUtil.newHashMap();
        } else if (MapUtil.isEmpty(map2)) {
            map2 = MapUtil.newHashMap();
        }
        HashSet key = new HashSet();
        key.addAll(map1.keySet());
        key.addAll(map2.keySet());
        HashMap map = new HashMap();
        for (Object t : key) {
            Object y;
            Object x = map1.get(t);
            V z = merge.apply(x, y = map2.get(t));
            if (z == null) continue;
            map.put(t, z);
        }
        return map;
    }

    public static <E> List<E> getLimitList(List<E> list, Integer pageNum, Integer pageSize) {
        if (list == null || list.size() == 0) {
            return list;
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = 10;
        }
        return list.stream().skip((long)(pageNum - 1) * (long)pageSize.intValue()).limit(pageSize.intValue()).toList();
    }

    private static <I, R> Function<I, R> castingIdentity() {
        return i -> i;
    }

    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{BigDecimal.ZERO}, (a, t) -> {
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    public static <T> Collector<T, ?, BigDecimal> maxBy(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{new BigDecimal(Long.MIN_VALUE)}, (a, t) -> {
            a[0] = a[0].max(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].max(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    public static <T> Collector<T, ?, BigDecimal> minBy(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{new BigDecimal(Long.MAX_VALUE)}, (a, t) -> {
            a[0] = a[0].min(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].min(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    public static <T> Collector<T, ?, BigDecimal> averagingBigDecimal(ToBigDecimalFunction<? super T> mapper, int newScale, RoundingMode roundingMode) {
        return new CollectorImpl<Object, BigDecimal[], BigDecimal>(() -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO}, (a, t) -> {
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
            a[1] = a[1].add(BigDecimal.ONE);
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0].divide(a[1], RoundingMode.HALF_UP).setScale(newScale, roundingMode), CH_NOID);
    }

    @SafeVarargs
    public static <K, V> Map<K, List<V>> concat(Map<K, V> ... maps) {
        if (ObjectUtils.isEmpty((Object[])maps)) {
            return Collections.emptyMap();
        }
        return Arrays.stream(maps).filter(Objects::nonNull).flatMap(map -> map.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    @SafeVarargs
    public static <T> Stream<T> concat(T[] ... ts) {
        if (ObjectUtils.isEmpty(ts)) {
            return Stream.empty();
        }
        return Arrays.stream(ts).filter(Objects::nonNull).flatMap(Arrays::stream);
    }

    @SafeVarargs
    public static <T, R> Stream<R> zip(Function<Stream<T>, Stream<R>> combinator, Stream<T> ... streams) {
        return ObjectUtils.isEmpty((Object[])streams) ? Stream.empty() : Stream.of(streams).flatMap(combinator);
    }

    public static <T> Predicate<T> distinct(Function<? super T, ?> function) {
        ConcurrentHashMap seen = new ConcurrentHashMap();
        return t -> seen.putIfAbsent(function.apply(t), Boolean.TRUE) == null;
    }

    public static <T> Stream<T> convert(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 16), false);
    }

    public static <T> Stream<T> convert(Enumeration<T> enumeration) {
        return StreamSupport.stream(EnumerationSpliterator.spliteratorUnknownSize(enumeration), false);
    }

    public static <T, R> Function<T, R> mapWithIndex(int initValue, BiFunction<T, Integer, R> biFunction) {
        AtomicInteger atomicInteger = new AtomicInteger(initValue);
        return t -> biFunction.apply(t, atomicInteger.getAndIncrement());
    }

    public static <T> Consumer<T> forEachWithIndex(int initValue, BiConsumer<T, Integer> biConsumer) {
        AtomicInteger atomicInteger = new AtomicInteger(initValue);
        return t -> biConsumer.accept(t, atomicInteger.getAndIncrement());
    }

    @SafeVarargs
    public static <T> Stream<T> cartesianProduct(BinaryOperator<T> aggregator, Supplier<Stream<T>> ... streams) {
        return Arrays.stream(streams).reduce((s1, s2) -> () -> StreamUtils.lambda$cartesianProduct$1((Supplier)s1, (Supplier)s2, aggregator)).orElse(Stream::empty).get();
    }

    private static /* synthetic */ Stream lambda$cartesianProduct$1(Supplier s1, Supplier s2, BinaryOperator aggregator) {
        return ((Stream)s1.get()).flatMap(arg_0 -> StreamUtils.lambda$cartesianProduct$2((Supplier)s2, aggregator, arg_0));
    }

    private static /* synthetic */ Stream lambda$cartesianProduct$2(Supplier s2, BinaryOperator aggregator, Object t1) {
        return ((Stream)s2.get()).map(t2 -> aggregator.apply(t1, t2));
    }

    public static interface FunctionWithIndex<T, R> {
        public R apply(T var1, long var2);
    }

    public static interface IntFunctionWithIndex<R> {
        public R apply(int var1, long var2);
    }

    public static interface LongFunctionWithIndex<R> {
        public R apply(long var1, long var3);
    }

    public static interface DoubleFunctionWithIndex<R> {
        public R apply(double var1, long var3);
    }

    public static class CollectorImpl<T, A, R>
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
            this(supplier, accumulator, combiner, StreamUtils.castingIdentity(), characteristics);
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

    @FunctionalInterface
    public static interface ToBigDecimalFunction<T> {
        public BigDecimal applyAsBigDecimal(T var1);
    }

    private static class TemporaryPair<A, B> {
        final A a;
        final B b;

        TemporaryPair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    private static abstract class MapWithIndexSpliterator<F extends Spliterator<?>, R, S extends MapWithIndexSpliterator<F, R, S>>
    implements Spliterator<R> {
        final F fromSpliterator;
        long index;

        MapWithIndexSpliterator(F fromSpliterator, long index) {
            this.fromSpliterator = fromSpliterator;
            this.index = index;
        }

        abstract S createSplit(F var1, long var2);

        public S trySplit() {
            Spliterator splitOrNull = this.fromSpliterator.trySplit();
            if (splitOrNull == null) {
                return null;
            }
            Spliterator split = splitOrNull;
            S result = this.createSplit(split, this.index);
            this.index += split.getExactSizeIfKnown();
            return result;
        }

        @Override
        public long estimateSize() {
            return this.fromSpliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return this.fromSpliterator.characteristics() & 0x4050;
        }
    }
}

