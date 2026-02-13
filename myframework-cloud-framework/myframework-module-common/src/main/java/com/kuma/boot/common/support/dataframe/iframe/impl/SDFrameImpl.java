/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WindowSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.ConsumerIndex;
import com.kuma.boot.common.support.dataframe.iframe.function.ListToOneFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.NumberFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.ReplenishFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.impl.AbstractDataFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.impl.WindowSDFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.item.FI4;
import com.kuma.boot.common.support.dataframe.iframe.support.DFList;
import com.kuma.boot.common.support.dataframe.iframe.support.DefaultJoin;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.MaxMin;
import com.kuma.boot.common.support.dataframe.iframe.support.NullFirstComparator;
import com.kuma.boot.common.support.dataframe.iframe.support.NullLastComparator;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;
import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import com.kuma.boot.common.support.dataframe.util.CollectorsPlusUtil;
import com.kuma.boot.common.support.dataframe.util.FrameUtil;
import com.kuma.boot.common.support.dataframe.util.ListUtils;
import com.kuma.boot.common.support.dataframe.util.MathUtils;
import com.kuma.boot.common.support.dataframe.util.PartitionList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SDFrameImpl<T>
extends AbstractDataFrameImpl<T>
implements SDFrame<T> {
    protected Stream<T> data;

    public SDFrameImpl(Stream<T> data) {
        List tmp = data.collect(Collectors.toList());
        if (ListUtils.isNotEmpty(tmp)) {
            this.fieldClass = tmp.get(0).getClass();
        }
        this.data = tmp.stream();
    }

    public SDFrameImpl(List<T> list) {
        if (list == null) {
            list = Collections.emptyList();
        }
        this.data = list.stream();
        if (!list.isEmpty()) {
            this.fieldClass = list.get(0).getClass();
        }
    }

    @Override
    public List<T> toLists() {
        List tmp = this.data.collect(Collectors.toList());
        this.data = tmp.stream();
        return tmp;
    }

    @Override
    public List<T> viewList() {
        List tmp = this.data.collect(Collectors.toList());
        this.data = tmp.stream();
        return tmp;
    }

    @Override
    public <R> SDFrameImpl<R> from(Stream<R> data) {
        return new SDFrameImpl<R>(data);
    }

    @Override
    public SDFrameImpl<T> forEachDo(Consumer<? super T> action) {
        this.forEach(action);
        return this;
    }

    @Override
    public SDFrameImpl<T> forEachParallel(Consumer<? super T> action) {
        ((Stream)this.viewList().stream().parallel()).forEach(action);
        return this;
    }

    @Override
    public SDFrameImpl<T> forEachDo(ConsumerIndex<? super T> action) {
        int index = 0;
        for (Object t : this) {
            action.accept(index++, t);
        }
        return this;
    }

    @Override
    public SDFrame<T> defaultScale(int scale) {
        this.initDefaultScale(scale, this.defaultRoundingMode);
        return this;
    }

    @Override
    public SDFrame<T> defaultScale(int scale, RoundingMode roundingMode) {
        this.initDefaultScale(scale, roundingMode);
        return this;
    }

    @Override
    public <R> SDFrameImpl<R> map(Function<T, R> map) {
        return this.returnDF(this.stream().map(map));
    }

    @Override
    public <R> SDFrame<R> mapParallel(Function<T, R> map) {
        return this.returnDF(((Stream)this.stream().parallel()).map(map));
    }

    @Override
    public <R extends Number> SDFrame<T> mapPercent(Function<T, R> get, SetFunction<T, BigDecimal> set) {
        return this.mapPercent((Function)get, (SetFunction)set, 2);
    }

    @Override
    public <R extends Number> SDFrame<T> mapPercent(Function<T, R> get, SetFunction<T, BigDecimal> set, int scale) {
        this.viewList().forEach(e -> {
            Number value = (Number)get.apply(e);
            BigDecimal percentageValue = MathUtils.percentage(MathUtils.toBigDecimal(value), scale);
            set.accept(e, percentageValue);
        });
        return this;
    }

    @Override
    public SDFrameImpl<List<T>> partition(int n) {
        return this.returnDF(new PartitionList<T>(this.viewList(), n));
    }

    @Override
    public <R, K> SDFrameImpl<R> join(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.joinList(other, on, join));
    }

    @Override
    public <R, K> SDFrameImpl<R> joinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.joinList(other, on, join, true));
    }

    @Override
    public <R, K> SDFrameImpl<R> join(IFrame<K> other, JoinOn<T, K> on) {
        return this.join((IFrame)other, (JoinOn)on, (Join)new DefaultJoin());
    }

    @Override
    public <K> SDFrameImpl<T> joinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.joinListLink(other, on, join);
        return this;
    }

    @Override
    public <K> SDFrameImpl<T> joinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.joinListLink(other, on, join, true);
        return this;
    }

    @Override
    public <R, K> SDFrameImpl<R> leftJoin(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.leftJoinList(other, on, join));
    }

    @Override
    public <R, K> SDFrameImpl<R> leftJoinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.leftJoinList(other, on, join, true));
    }

    @Override
    public <R, K> SDFrameImpl<R> leftJoin(IFrame<K> other, JoinOn<T, K> on) {
        return this.leftJoin((IFrame)other, (JoinOn)on, (Join)new DefaultJoin());
    }

    @Override
    public <K> SDFrameImpl<T> leftJoinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.leftJoinListLink(other, on, join);
        return this;
    }

    @Override
    public <K> SDFrame<T> leftJoinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.leftJoinListLink(other, on, join, true);
        return this;
    }

    @Override
    public <R, K> SDFrameImpl<R> rightJoin(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.rightJoinList(other, on, join));
    }

    @Override
    public <R, K> SDFrameImpl<R> rightJoinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.rightJoinList(other, on, join, true));
    }

    @Override
    public <R, K> SDFrameImpl<R> rightJoin(IFrame<K> other, JoinOn<T, K> on) {
        return this.rightJoin((IFrame)other, (JoinOn)on, (Join)new DefaultJoin());
    }

    @Override
    public <K> SDFrameImpl<T> rightJoinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.rightJoinListLink(other, on, join);
        return this;
    }

    @Override
    public <K> SDFrame<T> rightJoinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.rightJoinListLink(other, on, join, true);
        return this;
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> addRowNumberCol() {
        ArrayList result = new ArrayList();
        int index = 1;
        for (Object t : this) {
            result.add(new FI2(t, index++));
        }
        return this.returnDF(result);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> addRowNumberCol(Sorter<T> sorter) {
        return ((SDFrameImpl)this.sortAsc((Comparator)sorter)).addRowNumberCol();
    }

    @Override
    public SDFrameImpl<T> addRowNumberCol(SetFunction<T, Integer> set) {
        int index = 1;
        for (Object t : this) {
            set.accept(t, index++);
        }
        return this;
    }

    @Override
    public SDFrameImpl<T> addRowNumberCol(Sorter<T> sorter, SetFunction<T, Integer> set) {
        return ((SDFrameImpl)this.sortAsc((Comparator)sorter)).addRowNumberCol((SetFunction)set);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> addRankCol(Sorter<T> sorter) {
        return this.overRank((Window)Window.sortBy(sorter));
    }

    @Override
    public SDFrame<T> addRankCol(Sorter<T> sorter, SetFunction<T, Integer> set) {
        return this.fi2Frame((SDFrameImpl)this.addRankCol((Sorter)sorter), (SetFunction)set);
    }

    @Override
    public SDFrameImpl<FI2<T, String>> explodeString(Function<T, String> getFunction, String delimiter) {
        return this.returnDF(this.explodeStringStream(getFunction, delimiter));
    }

    @Override
    public SDFrameImpl<T> explodeString(Function<T, String> getFunction, SetFunction<T, String> setFunction, String delimiter) {
        return this.returnDF(this.fi2Stream(this.explodeStringStream(getFunction, delimiter), setFunction));
    }

    @Override
    public SDFrameImpl<FI2<T, String>> explodeJsonArray(Function<T, String> getFunction) {
        return this.returnDF(this.explodeJsonArrayStream(getFunction));
    }

    @Override
    public SDFrameImpl<T> explodeJsonArray(Function<T, String> getFunction, SetFunction<T, String> setFunction) {
        return this.returnDF(this.fi2Stream(this.explodeJsonArrayStream(getFunction), setFunction));
    }

    @Override
    public <E> SDFrameImpl<FI2<T, E>> explodeCollection(Function<T, ? extends Collection<E>> getFunction) {
        return this.returnDF(this.explodeCollectionStream(getFunction));
    }

    @Override
    public <E> SDFrame<T> explodeCollection(Function<T, ? extends Collection<E>> getFunction, SetFunction<T, E> setFunction) {
        return this.returnDF(this.fi2Stream(this.explodeCollectionStream(getFunction), setFunction));
    }

    @Override
    public <E> SDFrameImpl<FI2<T, E>> explodeCollectionArray(Function<T, ?> getFunction, Class<E> elementClass) {
        return this.returnDF(this.explodeCollectionArrayStream(getFunction, elementClass));
    }

    @Override
    public <E> SDFrameImpl<T> explodeCollectionArray(Function<T, ?> getFunction, SetFunction<T, E> setFunction, Class<E> elementClass) {
        return this.returnDF(this.fi2Stream(this.explodeCollectionArrayStream(getFunction, elementClass), setFunction));
    }

    @Override
    public Stream<T> stream() {
        return this.data;
    }

    @Override
    public long count() {
        List tmp = this.stream().collect(Collectors.toList());
        this.data = tmp.stream();
        return tmp.size();
    }

    @Override
    public SDFrameImpl<T> sortDesc(Comparator<T> comparator) {
        this.data = this.stream().sorted(comparator.reversed());
        return this;
    }

    @Override
    public <R extends Comparable<? super R>> SDFrameImpl<T> sortDesc(Function<T, R> function) {
        this.sortDesc((Comparator)NullLastComparator.comparing(function));
        return this;
    }

    @Override
    public SDFrameImpl<T> sortAsc(Comparator<T> comparator) {
        this.data = this.stream().sorted(comparator);
        return this;
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> sortAsc(Function<T, R> function) {
        this.sortAsc((Comparator)NullFirstComparator.comparing(function));
        return this;
    }

    @Override
    public SDFrame<T> cutFirstRank(Sorter<T> sorter, int n) {
        return ((SDFrameImpl)((SDFrameImpl)this.overRank((Window)Window.sortBy(sorter))).whereLe(FI2::getC2, Integer.valueOf(n))).map(FI2::getC1);
    }

    @Override
    public SDFrame<T> cutFirst(int n) {
        DFList<T> first = new DFList<T>(this.viewList()).first(n);
        List<T> build = first.build();
        return this.returnThis(build);
    }

    @Override
    public SDFrame<T> cutLast(int n) {
        DFList<T> first = new DFList<T>(this.viewList()).last(n);
        this.data = first.build().stream();
        return this;
    }

    @Override
    public SDFrame<T> cut(Integer startIndex, Integer endIndex) {
        return this.returnThis(this.getList(startIndex, endIndex));
    }

    @Override
    public SDFrame<T> cutPage(int page, int pageSize) {
        return this.returnThis(this.page(page, pageSize));
    }

    @Override
    public SDFrame<T> distinct() {
        this.data = this.stream().distinct();
        return this;
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> distinct(Function<T, R> function) {
        return this.distinct((Comparator)Comparator.comparing(function));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> distinct(Function<T, R> function, ListToOneFunction<T> listOneFunction) {
        return this.distinct((Comparator)Comparator.comparing(function), (ListToOneFunction)listOneFunction);
    }

    @Override
    public SDFrameImpl<T> distinct(Comparator<T> comparator) {
        ArrayList tmp = this.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet(comparator)), ArrayList::new));
        return this.returnThis(tmp);
    }

    @Override
    public SDFrameImpl<T> distinct(Comparator<T> comparator, ListToOneFunction<T> function) {
        return this.returnThis(this.distinctList(this.viewList(), comparator, function));
    }

    @Override
    public long countDistinct(Comparator<T> comparator) {
        return ((SDFrameImpl)this.distinct((Comparator)comparator)).count();
    }

    @Override
    public <R extends Comparable<R>> long countDistinct(Function<T, R> function) {
        return this.countDistinct(Comparator.comparing(function));
    }

    @Override
    public SDFrameImpl<T> where(Predicate<? super T> predicate) {
        return this.returnThis(this.stream().filter(predicate));
    }

    @Override
    public <R> SDFrameImpl<T> whereNull(Function<T, R> function) {
        return this.returnThis(this.whereNullStream(function));
    }

    @Override
    public <R> SDFrameImpl<T> whereNotNull(Function<T, R> function) {
        return this.returnThis(this.whereNotNullStream(function));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereBetween(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnThis(this.whereBetweenStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereBetweenN(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnThis(this.whereBetweenNStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereBetweenR(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnThis(this.whereBetweenRStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereBetweenL(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnThis(this.whereBetweenLStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereNotBetween(Function<T, R> function, R start, R end) {
        if (start == null || end == null) {
            return this;
        }
        return this.returnThis(this.whereNotBetweenStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereNotBetweenN(Function<T, R> function, R start, R end) {
        if (start == null || end == null) {
            return this;
        }
        return this.returnThis(this.whereNotBetweenNStream(function, start, end));
    }

    @Override
    public <R> SDFrameImpl<T> whereIn(Function<T, R> function, List<R> list) {
        if (list == null || list.isEmpty()) {
            return this;
        }
        return this.returnThis(this.whereInStream(function, list));
    }

    @Override
    public <R> SDFrameImpl<T> whereNotIn(Function<T, R> function, List<R> list) {
        if (list == null || list.isEmpty()) {
            return this;
        }
        return this.returnThis(this.whereNotInStream(function, list));
    }

    @Override
    public SDFrameImpl<T> whereTrue(Predicate<T> predicate) {
        return this.returnThis(this.stream().filter(predicate));
    }

    @Override
    public SDFrameImpl<T> whereNotTrue(Predicate<T> predicate) {
        return this.whereTrue((Predicate)predicate.negate());
    }

    @Override
    public <R> SDFrameImpl<T> whereEq(Function<T, R> function, R value) {
        return this.returnThis(this.whereEqStream(function, value));
    }

    @Override
    public <R> SDFrameImpl<T> whereNotEq(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereNotEqStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereGt(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereGtStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereGe(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereGeStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereLt(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereLtStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> SDFrameImpl<T> whereLe(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereLeStream(function, value));
    }

    @Override
    public <R> SDFrameImpl<T> whereLike(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereLikeStream(function, value));
    }

    @Override
    public <R> SDFrameImpl<T> whereNotLike(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereNotLikeStream(function, value));
    }

    @Override
    public <R> SDFrame<T> whereLikeLeft(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereLikeLeftStream(function, value));
    }

    @Override
    public <R> SDFrame<T> whereLikeRight(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnThis(this.whereLikeRightStream(function, value));
    }

    @Override
    public <K> SDFrameImpl<FI2<K, List<T>>> group(Function<? super T, ? extends K> key) {
        return this.returnDF(this.groupKey(key));
    }

    @Override
    public <K, R extends Number> SDFrameImpl<FI2<K, BigDecimal>> groupBySum(Function<T, K> key, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI2<K, BigDecimal>> collect = this.groupKey(key, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, R extends Number> SDFrameImpl<FI3<K, J, BigDecimal>> groupBySum(Function<T, K> key, Function<T, J> key2, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI3<K, J, BigDecimal>> collect = this.groupKey(key, key2, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, H, R extends Number> SDFrameImpl<FI4<K, J, H, BigDecimal>> groupBySum(Function<T, K> key, Function<T, J> key2, Function<T, H> key3, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI4<K, J, H, BigDecimal>> collect = this.groupKey(key, key2, key3, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K> SDFrameImpl<FI2<K, Long>> groupByCount(Function<T, K> key) {
        Collector counting = Collectors.counting();
        Map<K, Long> collect = this.stream().collect(Collectors.groupingBy(key, counting));
        return this.returnDF(FrameUtil.toListFI2(collect));
    }

    @Override
    public <K, J> SDFrameImpl<FI3<K, J, Long>> groupByCount(Function<T, K> key, Function<T, J> key2) {
        Collector counting = Collectors.counting();
        Map<K, Map<J, Long>> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, counting)));
        return this.returnDF(FrameUtil.toListFI3(collect));
    }

    @Override
    public <K, J, H> SDFrameImpl<FI4<K, J, H, Long>> groupByCount(Function<T, K> key, Function<T, J> key2, Function<T, H> key3) {
        Collector counting = Collectors.counting();
        Map<K, Map<J, Map<H, Long>>> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.groupingBy(key3, counting))));
        return this.returnDF(FrameUtil.toListFI4(collect));
    }

    @Override
    public <K, R extends Number> SDFrameImpl<FI3<K, BigDecimal, Long>> groupBySumCount(Function<T, K> key, NumberFunction<T, R> value) {
        List<T> dataList = this.viewList();
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI2<K, BigDecimal>> sumList = this.returnDF(dataList).groupKey(key, tBigDecimalCollector);
        List<T> countList = ((SDFrameImpl)this.returnDF(dataList).groupByCount((Function)key)).viewList();
        Map<Object, Long> countMap = countList.stream().collect(Collectors.toMap(FI2::getC1, FI2::getC2));
        List collect = sumList.stream().map((? super T e) -> new FI3(e.getC1(), (BigDecimal)e.getC2(), (Long)countMap.get(e.getC1()))).collect(Collectors.toList());
        return this.returnDF(collect);
    }

    @Override
    public <K, J, R extends Number> SDFrameImpl<FI4<K, J, BigDecimal, Long>> groupBySumCount(Function<T, K> key, Function<T, J> key2, NumberFunction<T, R> value) {
        List<T> dataList = this.viewList();
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI3<K, J, BigDecimal>> sumList = this.returnDF(dataList).groupKey(key, key2, tBigDecimalCollector);
        List<T> countList = ((SDFrameImpl)this.returnDF(dataList).groupByCount((Function)key, (Function)key2)).viewList();
        Map countMap = countList.stream().collect(Collectors.toMap(e -> String.valueOf(e.getC1()) + "_" + String.valueOf(e.getC2()), Function.identity()));
        List collect = sumList.stream().map((? super T e) -> {
            FI3 countItem = (FI3)countMap.get(String.valueOf(e.getC1()) + "_" + String.valueOf(e.getC2()));
            return new FI4(e.getC1(), e.getC2(), (BigDecimal)e.getC3(), (Long)countItem.getC3());
        }).collect(Collectors.toList());
        return this.returnDF(collect);
    }

    @Override
    public <K, R extends Number> SDFrameImpl<FI2<K, BigDecimal>> groupByAvg(Function<T, K> key, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, this.defaultScale, this.getOldRoundingMode());
        List<FI2<K, BigDecimal>> collect = this.groupKey(key, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, R extends Number> SDFrameImpl<FI3<K, J, BigDecimal>> groupByAvg(Function<T, K> key, Function<T, J> key2, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, this.defaultScale, this.getOldRoundingMode());
        List<FI3<K, J, BigDecimal>> collect = this.groupKey(key, key2, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, H, R extends Number> SDFrameImpl<FI4<K, J, H, BigDecimal>> groupByAvg(Function<T, K> key, Function<T, J> key2, Function<T, H> key3, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, this.defaultScale, this.getOldRoundingMode());
        List<FI4<K, J, H, BigDecimal>> collect = this.groupKey(key, key2, key3, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, V extends Comparable<? super V>> SDFrameImpl<FI2<K, T>> groupByMax(Function<T, K> key, Function<T, V> value) {
        Map<K, T> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListMaxFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(collect));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> SDFrameImpl<FI3<K, J, T>> groupByMax(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, T>> collect = this.groupToMap(key, key2, this.getListMaxFunction(value));
        return this.returnDF(FrameUtil.toListFI3(collect));
    }

    @Override
    public <K, V extends Comparable<? super V>> SDFrameImpl<FI2<K, V>> groupByMaxValue(Function<T, K> key, Function<T, V> value) {
        return ((SDFrameImpl)this.groupByMax((Function)key, (Function)value)).map(e -> new FI2(e.getC1(), (Comparable)this.getApplyValue(value, e.getC2())));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> SDFrameImpl<FI3<K, J, V>> groupByMaxValue(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        return ((SDFrameImpl)this.groupByMax((Function)key, (Function)key2, (Function)value)).map(e -> new FI3(e.getC1(), e.getC2(), (Comparable)this.getApplyValue(value, e.getC3())));
    }

    @Override
    public <K, V extends Comparable<? super V>> SDFrameImpl<FI2<K, T>> groupByMin(Function<T, K> key, Function<T, V> value) {
        Map<K, Object> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), e -> e.stream().min(Comparator.comparing(value)).orElse(null))));
        return this.returnDF(FrameUtil.toListFI2(collect));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> SDFrameImpl<FI3<K, J, T>> groupByMin(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, T>> collect = this.groupToMap(key, key2, this.getListMinFunction(value));
        return this.returnDF(FrameUtil.toListFI3(collect));
    }

    @Override
    public <K, V extends Comparable<? super V>> SDFrameImpl<FI2<K, V>> groupByMinValue(Function<T, K> key, Function<T, V> value) {
        return ((SDFrameImpl)this.groupByMin((Function)key, (Function)value)).map(e -> new FI2(e.getC1(), (Comparable)this.getApplyValue(value, e.getC2())));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> SDFrameImpl<FI3<K, J, V>> groupByMinValue(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        return ((SDFrameImpl)this.groupByMin((Function)key, (Function)key2, (Function)value)).map(e -> new FI3(e.getC1(), e.getC2(), (Comparable)this.getApplyValue(value, e.getC3())));
    }

    @Override
    public <K, V extends Comparable<? super V>> SDFrameImpl<FI2<K, MaxMin<V>>> groupByMaxMinValue(Function<T, K> key, Function<T, V> value) {
        Map<K, MaxMin<V>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinValueFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(map));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> SDFrameImpl<FI3<K, J, MaxMin<V>>> groupByMaxMinValue(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, MaxMin<V>>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinValueFunction(value)))));
        return this.returnDF(FrameUtil.toListFI3(map));
    }

    @Override
    public <K, V extends Comparable<? super V>> SDFrameImpl<FI2<K, MaxMin<T>>> groupByMaxMin(Function<T, K> key, Function<T, V> value) {
        Map<K, MaxMin<T>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(map));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> SDFrameImpl<FI3<K, J, MaxMin<T>>> groupByMaxMin(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, MaxMin<T>>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinFunction(value)))));
        return this.returnDF(FrameUtil.toListFI3(map));
    }

    @Override
    public WindowSDFrame<T> window(Window<T> window) {
        WindowSDFrameImpl<T> frame = new WindowSDFrameImpl<T>(window, this.stream());
        this.transmitMember(this, frame);
        return frame;
    }

    @Override
    public WindowSDFrame<T> window() {
        WindowSDFrameImpl<T> frame = new WindowSDFrameImpl<T>(this.emptyWindow, this.stream());
        this.transmitMember(this, frame);
        return frame;
    }

    public <F> SDFrameImpl<T> fi2Frame(SDFrameImpl<FI2<T, F>> frame, SetFunction<T, F> setFunction) {
        return ((SDFrameImpl)frame.forEachDo((T e) -> setFunction.accept(e.getC1(), e.getC2()))).map(FI2::getC1);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overRowNumber(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForRowNumber(overParam));
    }

    @Override
    public SDFrame<FI2<T, Integer>> overRowNumber() {
        return this.overRowNumber(this.emptyWindow);
    }

    @Override
    public SDFrameImpl<T> overRowNumberS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((SDFrameImpl)this.overRowNumber((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public SDFrame<T> overRowNumberS(SetFunction<T, Integer> setFunction) {
        return this.overRowNumberS((SetFunction)setFunction, this.emptyWindow);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overRank(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForRank(overParam));
    }

    @Override
    public SDFrameImpl<T> overRankS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((SDFrameImpl)this.overRank((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overDenseRank(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForDenseRank(overParam));
    }

    @Override
    public SDFrameImpl<T> overDenseRankS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((SDFrameImpl)this.overDenseRank((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public SDFrameImpl<FI2<T, BigDecimal>> overPercentRank(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForPercentRank(overParam));
    }

    @Override
    public SDFrameImpl<T> overPercentRankS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam) {
        return this.fi2Frame((SDFrameImpl)this.overPercentRank((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public SDFrameImpl<FI2<T, BigDecimal>> overCumeDist(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForCumeDist(overParam));
    }

    @Override
    public SDFrameImpl<T> overCumeDistS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam) {
        return this.fi2Frame((SDFrameImpl)this.overCumeDist((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLag(Window<T> overParam, Function<T, F> field, int n) {
        return this.returnDF(this.windowFunctionForLag(overParam, field, n));
    }

    @Override
    public <F> SDFrame<T> overLagS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field, int n) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overLag((Window)overParam, (Function)field, n), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLag(Function<T, F> field, int n) {
        return this.overLag(this.emptyWindow, (Function)field, n);
    }

    @Override
    public <F> SDFrame<T> overLagS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overLag((Function)field, n), setFunction);
    }

    @Override
    public <F> SDFrame<T> overLeadS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field, int n) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overLead((Window)overParam, (Function)field, n), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLead(Window<T> overParam, Function<T, F> field, int n) {
        return this.returnDF(this.windowFunctionForLead(overParam, field, n));
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLead(Function<T, F> field, int n) {
        return this.overLead(this.emptyWindow, (Function)field, n);
    }

    @Override
    public <F> SDFrame<T> overLeadS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overLead((Function)field, n), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overNthValue(Window<T> overParam, Function<T, F> field, int n) {
        return this.returnDF(this.windowFunctionForNthValue(overParam, field, n));
    }

    @Override
    public <F> SDFrame<T> overNthValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field, int n) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overNthValue((Window)overParam, (Function)field, n), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overNthValue(Function<T, F> field, int n) {
        return this.overNthValue(this.emptyWindow, (Function)field, n);
    }

    @Override
    public <F> SDFrameImpl<T> overNthValueS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overNthValue((Function)field, n), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overFirstValue(Window<T> overParam, Function<T, F> field) {
        return this.overNthValue((Window)overParam, (Function)field, 1);
    }

    @Override
    public <F> SDFrame<T> overFirstValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overFirstValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overFirstValue(Function<T, F> field) {
        return this.overFirstValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> SDFrameImpl<T> overFirstValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overFirstValue((Function)field), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLastValue(Window<T> overParam, Function<T, F> field) {
        return this.overNthValue((Window)overParam, (Function)field, -1);
    }

    @Override
    public <F> SDFrameImpl<T> overLastValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overLastValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, F>> overLastValue(Function<T, F> field) {
        return this.overLastValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> SDFrame<T> overLastValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overLastValue((Function)field), setFunction);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, BigDecimal>> overSum(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForSum(overParam, field));
    }

    @Override
    public <F> SDFrameImpl<FI2<T, BigDecimal>> overSum(Function<T, F> field) {
        return this.overSum(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> SDFrameImpl<T> overSumS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overSum((Window)overParam, (Function)field), (SetFunction<T, F>)setFunction);
    }

    @Override
    public <F> SDFrameImpl<T> overSumS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.overSumS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public <F> SDFrameImpl<FI2<T, BigDecimal>> overAvg(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForAvg(overParam, field));
    }

    @Override
    public <F> SDFrameImpl<FI2<T, BigDecimal>> overAvg(Function<T, F> field) {
        return this.overAvg(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> SDFrameImpl<T> overAvgS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overAvg((Window)overParam, (Function)field), (SetFunction<T, F>)setFunction);
    }

    @Override
    public <F> SDFrameImpl<T> overAvgS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.overAvgS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<FI2<T, F>> overMaxValue(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForMaxValue(overParam, field));
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<FI2<T, F>> overMaxValue(Function<T, F> field) {
        return this.overMaxValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<T> overMaxValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overMaxValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<T> overMaxValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.overMaxValueS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<FI2<T, F>> overMinValue(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForMinValue(overParam, field));
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<FI2<T, F>> overMinValue(Function<T, F> field) {
        return this.overMinValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<T> overMinValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((SDFrameImpl<FI2<T, F>>)this.overMinValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F extends Comparable<? super F>> SDFrameImpl<T> overMinValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.overMinValueS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overCount(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForCount(overParam));
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overCount() {
        return this.overCount(this.emptyWindow);
    }

    @Override
    public SDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((SDFrameImpl)this.overCount((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public SDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction) {
        return this.overCountS((SetFunction)setFunction, this.emptyWindow);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overNtile(int n) {
        return this.overNtile(this.emptyWindow, n);
    }

    @Override
    public SDFrameImpl<FI2<T, Integer>> overNtile(Window<T> overParam, int n) {
        return this.returnDF(this.windowFunctionForNtile(overParam, n));
    }

    @Override
    public SDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, Window<T> overParam, int n) {
        return this.fi2Frame((SDFrameImpl)this.overNtile((Window)overParam, n), (SetFunction)setFunction);
    }

    @Override
    public SDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, int n) {
        return this.overNtileS((SetFunction)setFunction, this.emptyWindow, n);
    }

    @Override
    public SDFrameImpl<T> unionAll(IFrame<T> other) {
        ArrayList<T> ts = new ArrayList<T>(this.viewList());
        ts.addAll(other.toLists());
        return this.returnDF(ts);
    }

    @Override
    public SDFrameImpl<T> unionAll(Collection<T> other) {
        ArrayList<T> ts = new ArrayList<T>(this.viewList());
        ts.addAll(other);
        return this.returnDF(ts);
    }

    @Override
    public SDFrameImpl<T> union(IFrame<T> other) {
        return this.returnDF(this.unionList(this.viewList(), other.toLists()));
    }

    @Override
    public SDFrameImpl<T> union(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.unionList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public SDFrameImpl<T> union(Collection<T> other) {
        return this.returnDF(this.unionList(this.viewList(), other));
    }

    @Override
    public SDFrameImpl<T> union(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.unionList(this.viewList(), other, comparator));
    }

    @Override
    public SDFrameImpl<T> retainAll(IFrame<T> other) {
        return this.returnDF(this.retainAllList(this.viewList(), other.toLists()));
    }

    @Override
    public SDFrameImpl<T> retainAll(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.retainAllList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public SDFrameImpl<T> retainAll(Collection<T> other) {
        return this.returnDF(this.retainAllList(this.viewList(), other));
    }

    @Override
    public SDFrameImpl<T> retainAll(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.retainAllList(this.viewList(), other, comparator));
    }

    @Override
    public SDFrameImpl<T> intersection(IFrame<T> other) {
        return this.returnDF(this.intersectionList(this.viewList(), other.toLists()));
    }

    @Override
    public SDFrameImpl<T> intersection(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.intersectionList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public SDFrameImpl<T> intersection(Collection<T> other) {
        return this.returnDF(this.intersectionList(this.viewList(), other));
    }

    @Override
    public SDFrameImpl<T> intersection(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.intersectionList(this.viewList(), other, comparator));
    }

    @Override
    public SDFrameImpl<T> different(IFrame<T> other) {
        return this.returnDF(this.differentList(this.viewList(), other.toLists()));
    }

    @Override
    public SDFrameImpl<T> different(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.differentList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public SDFrameImpl<T> different(Collection<T> other) {
        return this.returnDF(this.differentList(this.viewList(), other));
    }

    @Override
    public SDFrame<T> different(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.differentList(this.viewList(), other, comparator));
    }

    @Override
    public <G, C> SDFrameImpl<T> replenish(Function<T, G> groupDim, Function<T, C> collectDim, List<C> allDim, ReplenishFunction<G, C, T> getEmptyObject) {
        return this.returnDF(SDFrameImpl.replenish(this.viewList(), groupDim, collectDim, allDim, getEmptyObject));
    }

    @Override
    public <C> SDFrameImpl<T> replenish(Function<T, C> collectDim, List<C> allDim, Function<C, T> getEmptyObject) {
        return this.returnDF(SDFrameImpl.replenish(this.viewList(), collectDim, allDim, getEmptyObject));
    }

    @Override
    public <G, C> SDFrameImpl<T> replenish(Function<T, G> groupDim, Function<T, C> collectDim, ReplenishFunction<G, C, T> getEmptyObject) {
        return this.returnDF(SDFrameImpl.replenish(this.viewList(), groupDim, collectDim, getEmptyObject));
    }

    protected SDFrameImpl<T> returnThis(Stream<T> stream) {
        this.data = stream;
        return this;
    }

    protected SDFrameImpl<T> returnThis(List<T> dataList) {
        this.data = dataList.stream();
        return this;
    }

    protected <R> SDFrameImpl<R> returnDF(List<R> dataList) {
        SDFrameImpl<R> frame = new SDFrameImpl<R>(dataList);
        this.transmitMember(this, frame);
        return frame;
    }

    protected <R> SDFrameImpl<R> returnDF(Stream<R> stream) {
        SDFrameImpl<R> frame = new SDFrameImpl<R>(stream);
        this.transmitMember(this, frame);
        return frame;
    }
}

