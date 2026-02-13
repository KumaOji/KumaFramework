/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WindowJDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.ConsumerIndex;
import com.kuma.boot.common.support.dataframe.iframe.function.ListToOneFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.NumberFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.ReplenishFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.impl.AbstractDataFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.impl.WindowJDFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.item.FI4;
import com.kuma.boot.common.support.dataframe.iframe.support.DFList;
import com.kuma.boot.common.support.dataframe.iframe.support.DefaultJoin;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.MaxMin;
import com.kuma.boot.common.support.dataframe.iframe.support.NullLastComparator;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;
import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import com.kuma.boot.common.support.dataframe.util.CollectorsPlusUtil;
import com.kuma.boot.common.support.dataframe.util.FrameUtil;
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

public class JDFrameImpl<T>
extends AbstractDataFrameImpl<T>
implements JDFrame<T> {
    protected List<T> dataList;

    public JDFrameImpl(List<T> list) {
        if (list == null) {
            list = Collections.emptyList();
        }
        this.dataList = list;
        if (!this.dataList.isEmpty()) {
            this.fieldClass = this.dataList.get(0).getClass();
        }
    }

    @Override
    public List<T> toLists() {
        return this.dataList;
    }

    @Override
    protected List<T> viewList() {
        return this.dataList;
    }

    @Override
    public Stream<T> stream() {
        return this.dataList.stream();
    }

    @Override
    public long count() {
        return this.dataList.size();
    }

    @Override
    public <R> JDFrameImpl<R> from(Stream<R> stream) {
        return new JDFrameImpl(stream.collect(Collectors.toList()));
    }

    @Override
    public <R> JDFrameImpl<R> from(List<R> list) {
        return new JDFrameImpl<R>(list);
    }

    @Override
    public JDFrameImpl<T> forEachDo(Consumer<? super T> action) {
        this.forEach(action);
        return this;
    }

    @Override
    public JDFrameImpl<T> forEachParallel(Consumer<? super T> action) {
        ((Stream)this.stream().parallel()).forEach(action);
        return this;
    }

    @Override
    public JDFrameImpl<T> forEachDo(ConsumerIndex<? super T> action) {
        int index = 0;
        for (Object t : this) {
            action.accept(index++, t);
        }
        return this;
    }

    @Override
    public JDFrameImpl<T> defaultScale(int scale) {
        this.initDefaultScale(scale, this.defaultRoundingMode);
        return this;
    }

    @Override
    public JDFrameImpl<T> defaultScale(int scale, RoundingMode roundingMode) {
        this.initDefaultScale(scale, roundingMode);
        return this;
    }

    @Override
    public <R> JDFrameImpl<R> map(Function<T, R> map) {
        return this.returnDF(this.stream().map(map));
    }

    @Override
    public <R> JDFrame<R> mapParallel(Function<T, R> map) {
        return this.returnDF(((Stream)this.stream().parallel()).map(map));
    }

    @Override
    public <R extends Number> JDFrameImpl<T> mapPercent(Function<T, R> get, SetFunction<T, BigDecimal> set) {
        return this.mapPercent((Function)get, (SetFunction)set, 2);
    }

    @Override
    public <R extends Number> JDFrameImpl<T> mapPercent(Function<T, R> get, SetFunction<T, BigDecimal> set, int scale) {
        this.viewList().forEach(e -> {
            Number value = (Number)get.apply(e);
            BigDecimal percentageValue = MathUtils.percentage(MathUtils.toBigDecimal(value), scale);
            set.accept(e, percentageValue);
        });
        return this;
    }

    @Override
    public JDFrameImpl<List<T>> partition(int n) {
        return this.returnDF(new PartitionList<T>(this.viewList(), n));
    }

    @Override
    public <R, K> JDFrameImpl<R> join(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.joinList(other, on, join));
    }

    @Override
    public <R, K> JDFrameImpl<R> joinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.joinList(other, on, join, true));
    }

    @Override
    public <R, K> JDFrameImpl<R> join(IFrame<K> other, JoinOn<T, K> on) {
        return this.join((IFrame)other, (JoinOn)on, (Join)new DefaultJoin());
    }

    @Override
    public <K> JDFrameImpl<T> joinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.joinListLink(other, on, join);
        return this;
    }

    @Override
    public <K> JDFrameImpl<T> joinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.joinListLink(other, on, join, true);
        return this;
    }

    @Override
    public <R, K> JDFrameImpl<R> leftJoin(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.leftJoinList(other, on, join));
    }

    @Override
    public <R, K> JDFrameImpl<R> leftJoinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.leftJoinList(other, on, join, true));
    }

    @Override
    public <R, K> JDFrameImpl<R> leftJoin(IFrame<K> other, JoinOn<T, K> on) {
        return this.leftJoin((IFrame)other, (JoinOn)on, (Join)new DefaultJoin());
    }

    @Override
    public <K> JDFrameImpl<T> leftJoinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.leftJoinListLink(other, on, join);
        return this;
    }

    @Override
    public <K> JDFrameImpl<T> leftJoinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.leftJoinListLink(other, on, join, true);
        return this;
    }

    @Override
    public <R, K> JDFrameImpl<R> rightJoin(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.rightJoinList(other, on, join));
    }

    @Override
    public <R, K> JDFrameImpl<R> rightJoinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.returnDF(this.rightJoinList(other, on, join, true));
    }

    @Override
    public <R, K> JDFrameImpl<R> rightJoin(IFrame<K> other, JoinOn<T, K> on) {
        return this.rightJoin((IFrame)other, (JoinOn)on, (Join)new DefaultJoin());
    }

    @Override
    public <K> JDFrameImpl<T> rightJoinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.rightJoinListLink(other, on, join);
        return this;
    }

    @Override
    public <K> JDFrame<T> rightJoinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.rightJoinListLink(other, on, join, true);
        return this;
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> addRowNumberCol() {
        ArrayList result = new ArrayList();
        int index = 1;
        for (Object t : this) {
            result.add(new FI2(t, index++));
        }
        return this.returnDF(result);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> addRowNumberCol(Sorter<T> sorter) {
        return ((JDFrameImpl)this.sortAsc((Comparator)sorter)).addRowNumberCol();
    }

    @Override
    public JDFrameImpl<T> addRowNumberCol(SetFunction<T, Integer> set) {
        int index = 1;
        for (Object t : this) {
            set.accept(t, index++);
        }
        return this;
    }

    @Override
    public JDFrameImpl<T> addRowNumberCol(Sorter<T> sorter, SetFunction<T, Integer> set) {
        return ((JDFrameImpl)this.sortAsc((Comparator)sorter)).addRowNumberCol((SetFunction)set);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> addRankCol(Sorter<T> sorter) {
        return this.overRank((Window)Window.sortBy(sorter));
    }

    @Override
    public JDFrameImpl<T> addRankCol(Sorter<T> sorter, SetFunction<T, Integer> set) {
        return this.fi2Frame((JDFrameImpl)this.addRankCol((Sorter)sorter), (SetFunction)set);
    }

    @Override
    public JDFrameImpl<FI2<T, String>> explodeString(Function<T, String> getFunction, String delimiter) {
        return this.returnDF(this.explodeStringStream(getFunction, delimiter));
    }

    @Override
    public JDFrameImpl<T> explodeString(Function<T, String> getFunction, SetFunction<T, String> setFunction, String delimiter) {
        return this.returnDF(this.fi2Stream(this.explodeStringStream(getFunction, delimiter), setFunction));
    }

    @Override
    public JDFrameImpl<FI2<T, String>> explodeJsonArray(Function<T, String> getFunction) {
        return this.returnDF(this.explodeJsonArrayStream(getFunction));
    }

    @Override
    public JDFrameImpl<T> explodeJsonArray(Function<T, String> getFunction, SetFunction<T, String> setFunction) {
        return this.returnDF(this.fi2Stream(this.explodeJsonArrayStream(getFunction), setFunction));
    }

    @Override
    public <E> JDFrameImpl<FI2<T, E>> explodeCollection(Function<T, ? extends Collection<E>> getFunction) {
        return this.returnDF(this.explodeCollectionStream(getFunction));
    }

    @Override
    public <E> JDFrame<T> explodeCollection(Function<T, ? extends Collection<E>> getFunction, SetFunction<T, E> setFunction) {
        return this.returnDF(this.fi2Stream(this.explodeCollectionStream(getFunction), setFunction));
    }

    @Override
    public <E> JDFrameImpl<FI2<T, E>> explodeCollectionArray(Function<T, ?> getFunction, Class<E> elementClass) {
        return this.returnDF(this.explodeCollectionArrayStream(getFunction, elementClass));
    }

    @Override
    public <E> JDFrame<T> explodeCollectionArray(Function<T, ?> getFunction, SetFunction<T, E> setFunction, Class<E> elementClass) {
        return this.returnDF(this.fi2Stream(this.explodeCollectionArrayStream(getFunction, elementClass), setFunction));
    }

    @Override
    public JDFrameImpl<T> sortDesc(Comparator<T> comparator) {
        this.dataList.sort(comparator.reversed());
        return this;
    }

    @Override
    public <R extends Comparable<? super R>> JDFrameImpl<T> sortDesc(Function<T, R> function) {
        return this.sortDesc((Comparator)NullLastComparator.comparing(function));
    }

    @Override
    public JDFrameImpl<T> sortAsc(Comparator<T> comparator) {
        this.dataList.sort(comparator);
        return this;
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> sortAsc(Function<T, R> function) {
        return this.sortAsc((Comparator)NullLastComparator.comparing(function));
    }

    @Override
    public JDFrameImpl<T> cutFirstRank(Sorter<T> sorter, int n) {
        return ((JDFrameImpl)((JDFrameImpl)this.overRank((Window)Window.sortBy(sorter))).whereLe(FI2::getC2, Integer.valueOf(n))).map(FI2::getC1);
    }

    @Override
    public JDFrameImpl<T> cutFirst(int n) {
        DFList<T> first = new DFList<T>(this.viewList()).first(n);
        return this.returnDF(first.build());
    }

    @Override
    public JDFrameImpl<T> cutLast(int n) {
        DFList<T> first = new DFList<T>(this.viewList()).last(n);
        return this.returnDF(first.build());
    }

    @Override
    public JDFrameImpl<T> cut(Integer startIndex, Integer endIndex) {
        return this.returnDF(this.getList(startIndex, endIndex));
    }

    @Override
    public JDFrame<T> cutPage(int page, int pageSize) {
        return this.returnDF(this.page(page, pageSize));
    }

    @Override
    public JDFrameImpl<T> distinct() {
        return this.returnDF(this.stream().distinct());
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> distinct(Function<T, R> function) {
        return this.distinct((Comparator)Comparator.comparing(function));
    }

    @Override
    public <R extends Comparable<R>> JDFrame<T> distinct(Function<T, R> function, ListToOneFunction<T> listOneFunction) {
        return this.distinct((Comparator)Comparator.comparing(function), (ListToOneFunction)listOneFunction);
    }

    @Override
    public JDFrameImpl<T> distinct(Comparator<T> comparator) {
        ArrayList tmp = this.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet(comparator)), ArrayList::new));
        return this.returnDF(tmp);
    }

    @Override
    public JDFrameImpl<T> distinct(Comparator<T> comparator, ListToOneFunction<T> function) {
        return this.returnDF(this.distinctList(this.viewList(), comparator, function));
    }

    @Override
    public JDFrameImpl<T> where(Predicate<? super T> predicate) {
        return this.returnDF(this.stream().filter(predicate));
    }

    @Override
    public long countDistinct(Comparator<T> comparator) {
        return ((JDFrameImpl)this.distinct((Comparator)comparator)).count();
    }

    @Override
    public <R extends Comparable<R>> long countDistinct(Function<T, R> function) {
        return this.countDistinct(Comparator.comparing(function));
    }

    @Override
    public <R> JDFrameImpl<T> whereNull(Function<T, R> function) {
        return this.returnDF(this.whereNullStream(function));
    }

    @Override
    public <R> JDFrameImpl<T> whereNotNull(Function<T, R> function) {
        return this.returnDF(this.whereNotNullStream(function));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereBetween(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnDF(this.whereBetweenStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereBetweenN(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnDF(this.whereBetweenNStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereBetweenR(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnDF(this.whereBetweenRStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereBetweenL(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return this.returnDF(this.whereBetweenLStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereNotBetween(Function<T, R> function, R start, R end) {
        if (start == null || end == null) {
            return this;
        }
        return this.returnDF(this.whereNotBetweenStream(function, start, end));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereNotBetweenN(Function<T, R> function, R start, R end) {
        if (start == null || end == null) {
            return this;
        }
        return this.returnDF(this.whereNotBetweenNStream(function, start, end));
    }

    @Override
    public <R> JDFrame<T> whereIn(Function<T, R> function, List<R> list) {
        if (list == null || list.isEmpty()) {
            return this;
        }
        return this.returnDF(this.whereInStream(function, list));
    }

    @Override
    public <R> JDFrame<T> whereNotIn(Function<T, R> function, List<R> list) {
        if (list == null || list.isEmpty()) {
            return this;
        }
        return this.returnDF(this.whereNotInStream(function, list));
    }

    @Override
    public JDFrame<T> whereTrue(Predicate<T> predicate) {
        return this.returnDF(this.stream().filter(predicate));
    }

    @Override
    public JDFrame<T> whereNotTrue(Predicate<T> predicate) {
        return this.whereTrue((Predicate)predicate.negate());
    }

    @Override
    public <R> JDFrame<T> whereEq(Function<T, R> function, R value) {
        return this.returnDF(this.whereEqStream(function, value));
    }

    @Override
    public <R> JDFrame<T> whereNotEq(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereNotEqStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereGt(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereGtStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereGe(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereGeStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereLt(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereLtStream(function, value));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> whereLe(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereLeStream(function, value));
    }

    @Override
    public <R> JDFrame<T> whereLike(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereLikeStream(function, value));
    }

    @Override
    public <R> JDFrame<T> whereNotLike(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereNotLikeStream(function, value));
    }

    @Override
    public <R> JDFrame<T> whereLikeLeft(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereLikeLeftStream(function, value));
    }

    @Override
    public <R> JDFrame<T> whereLikeRight(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return this.returnDF(this.whereLikeRightStream(function, value));
    }

    @Override
    public <K> JDFrameImpl<FI2<K, List<T>>> group(Function<? super T, ? extends K> key) {
        return this.returnDF(this.groupKey(key));
    }

    @Override
    public <K, R extends Number> JDFrameImpl<FI2<K, BigDecimal>> groupBySum(Function<T, K> key, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI2<K, BigDecimal>> collect = this.groupKey(key, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, R extends Number> JDFrameImpl<FI3<K, J, BigDecimal>> groupBySum(Function<T, K> key, Function<T, J> key2, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI3<K, J, BigDecimal>> collect = this.groupKey(key, key2, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, H, R extends Number> JDFrameImpl<FI4<K, J, H, BigDecimal>> groupBySum(Function<T, K> key, Function<T, J> key2, Function<T, H> key3, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI4<K, J, H, BigDecimal>> collect = this.groupKey(key, key2, key3, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K> JDFrameImpl<FI2<K, Long>> groupByCount(Function<T, K> key) {
        Collector counting = Collectors.counting();
        Map<K, Long> collect = this.stream().collect(Collectors.groupingBy(key, counting));
        return this.returnDF(FrameUtil.toListFI2(collect));
    }

    @Override
    public <K, J> JDFrameImpl<FI3<K, J, Long>> groupByCount(Function<T, K> key, Function<T, J> key2) {
        Collector counting = Collectors.counting();
        Map<K, Map<J, Long>> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, counting)));
        return this.returnDF(FrameUtil.toListFI3(collect));
    }

    @Override
    public <K, J, H> JDFrameImpl<FI4<K, J, H, Long>> groupByCount(Function<T, K> key, Function<T, J> key2, Function<T, H> key3) {
        Collector counting = Collectors.counting();
        Map<K, Map<J, Map<H, Long>>> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.groupingBy(key3, counting))));
        return this.returnDF(FrameUtil.toListFI4(collect));
    }

    @Override
    public <K, R extends Number> JDFrameImpl<FI3<K, BigDecimal, Long>> groupBySumCount(Function<T, K> key, NumberFunction<T, R> value) {
        List<T> dataList = this.viewList();
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI2<K, BigDecimal>> sumList = this.returnDF(dataList).groupKey(key, tBigDecimalCollector);
        List<T> countList = ((JDFrameImpl)this.from(dataList).groupByCount((Function)key)).viewList();
        Map<Object, Long> countMap = countList.stream().collect(Collectors.toMap(FI2::getC1, FI2::getC2));
        List collect = sumList.stream().map((? super T e) -> new FI3(e.getC1(), (BigDecimal)e.getC2(), (Long)countMap.get(e.getC1()))).collect(Collectors.toList());
        return this.returnDF(collect);
    }

    @Override
    public <K, J, R extends Number> JDFrameImpl<FI4<K, J, BigDecimal, Long>> groupBySumCount(Function<T, K> key, Function<T, J> key2, NumberFunction<T, R> value) {
        List<T> dataList = this.viewList();
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimalForNumber(value);
        List<FI3<K, J, BigDecimal>> sumList = this.returnDF(dataList).groupKey(key, key2, tBigDecimalCollector);
        List<T> countList = ((JDFrameImpl)this.from(dataList).groupByCount((Function)key, (Function)key2)).viewList();
        Map countMap = countList.stream().collect(Collectors.toMap(e -> String.valueOf(e.getC1()) + "_" + String.valueOf(e.getC2()), Function.identity()));
        List collect = sumList.stream().map((? super T e) -> {
            FI3 countItem = (FI3)countMap.get(String.valueOf(e.getC1()) + "_" + String.valueOf(e.getC2()));
            return new FI4(e.getC1(), e.getC2(), (BigDecimal)e.getC3(), (Long)countItem.getC3());
        }).collect(Collectors.toList());
        return this.returnDF(collect);
    }

    @Override
    public <K, R extends Number> JDFrameImpl<FI2<K, BigDecimal>> groupByAvg(Function<T, K> key, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, this.defaultScale, this.getOldRoundingMode());
        List<FI2<K, BigDecimal>> collect = this.groupKey(key, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, R extends Number> JDFrameImpl<FI3<K, J, BigDecimal>> groupByAvg(Function<T, K> key, Function<T, J> key2, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, this.defaultScale, this.getOldRoundingMode());
        List<FI3<K, J, BigDecimal>> collect = this.groupKey(key, key2, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, J, H, R extends Number> JDFrameImpl<FI4<K, J, H, BigDecimal>> groupByAvg(Function<T, K> key, Function<T, J> key2, Function<T, H> key3, NumberFunction<T, R> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, this.defaultScale, this.getOldRoundingMode());
        List<FI4<K, J, H, BigDecimal>> collect = this.groupKey(key, key2, key3, tBigDecimalCollector);
        return this.returnDF(collect);
    }

    @Override
    public <K, V extends Comparable<? super V>> JDFrameImpl<FI2<K, T>> groupByMax(Function<T, K> key, Function<T, V> value) {
        Map<K, T> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListMaxFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(collect));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> JDFrameImpl<FI3<K, J, T>> groupByMax(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, T>> collect = this.groupToMap(key, key2, this.getListMaxFunction(value));
        return this.returnDF(FrameUtil.toListFI3(collect));
    }

    @Override
    public <K, V extends Comparable<? super V>> JDFrameImpl<FI2<K, V>> groupByMaxValue(Function<T, K> key, Function<T, V> value) {
        return ((JDFrameImpl)this.groupByMax((Function)key, (Function)value)).map(e -> new FI2(e.getC1(), (Comparable)this.getApplyValue(value, e.getC2())));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> JDFrameImpl<FI3<K, J, V>> groupByMaxValue(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        return ((JDFrameImpl)this.groupByMax((Function)key, (Function)key2, (Function)value)).map(e -> new FI3(e.getC1(), e.getC2(), (Comparable)this.getApplyValue(value, e.getC3())));
    }

    @Override
    public <K, V extends Comparable<? super V>> JDFrameImpl<FI2<K, T>> groupByMin(Function<T, K> key, Function<T, V> value) {
        Map<K, T> collect = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListMinFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(collect));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> JDFrameImpl<FI3<K, J, T>> groupByMin(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, T>> collect = this.groupToMap(key, key2, this.getListMinFunction(value));
        return this.returnDF(FrameUtil.toListFI3(collect));
    }

    @Override
    public <K, V extends Comparable<? super V>> JDFrameImpl<FI2<K, V>> groupByMinValue(Function<T, K> key, Function<T, V> value) {
        return ((JDFrameImpl)this.groupByMin((Function)key, (Function)value)).map(e -> new FI2(e.getC1(), (Comparable)this.getApplyValue(value, e.getC2())));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> JDFrameImpl<FI3<K, J, V>> groupByMinValue(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        return ((JDFrameImpl)this.groupByMin((Function)key, (Function)key2, (Function)value)).map(e -> new FI3(e.getC1(), e.getC2(), (Comparable)this.getApplyValue(value, e.getC3())));
    }

    @Override
    public <K, V extends Comparable<? super V>> JDFrameImpl<FI2<K, MaxMin<V>>> groupByMaxMinValue(Function<T, K> key, Function<T, V> value) {
        Map<K, MaxMin<V>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinValueFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(map));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> JDFrameImpl<FI3<K, J, MaxMin<V>>> groupByMaxMinValue(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, MaxMin<V>>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinValueFunction(value)))));
        return this.returnDF(FrameUtil.toListFI3(map));
    }

    @Override
    public <K, V extends Comparable<? super V>> JDFrameImpl<FI2<K, MaxMin<T>>> groupByMaxMin(Function<T, K> key, Function<T, V> value) {
        Map<K, MaxMin<T>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinFunction(value))));
        return this.returnDF(FrameUtil.toListFI2(map));
    }

    @Override
    public <K, J, V extends Comparable<? super V>> JDFrameImpl<FI3<K, J, MaxMin<T>>> groupByMaxMin(Function<T, K> key, Function<T, J> key2, Function<T, V> value) {
        Map<K, Map<J, MaxMin<T>>> map = this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.collectingAndThen(Collectors.toList(), this.getListGroupMaxMinFunction(value)))));
        return this.returnDF(FrameUtil.toListFI3(map));
    }

    @Override
    public WindowJDFrame<T> window(Window<T> window) {
        WindowJDFrameImpl<T> frame = new WindowJDFrameImpl<T>(window, this.dataList);
        this.transmitMember(this, frame);
        return frame;
    }

    @Override
    public WindowJDFrame<T> window() {
        WindowJDFrameImpl<T> frame = new WindowJDFrameImpl<T>(this.emptyWindow, this.dataList);
        this.transmitMember(this, frame);
        return frame;
    }

    public <F> JDFrameImpl<T> fi2Frame(JDFrameImpl<FI2<T, F>> frame, SetFunction<T, F> setFunction) {
        return ((JDFrameImpl)frame.forEachDo((T e) -> setFunction.accept(e.getC1(), e.getC2()))).map(FI2::getC1);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overRowNumber(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForRowNumber(overParam));
    }

    @Override
    public JDFrame<FI2<T, Integer>> overRowNumber() {
        return this.overRowNumber(this.emptyWindow);
    }

    @Override
    public JDFrameImpl<T> overRowNumberS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((JDFrameImpl)this.overRowNumber((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public JDFrame<T> overRowNumberS(SetFunction<T, Integer> setFunction) {
        return this.overRowNumberS((SetFunction)setFunction, this.emptyWindow);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overRank(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForRank(overParam));
    }

    @Override
    public JDFrameImpl<T> overRankS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((JDFrameImpl)this.overRank((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overDenseRank(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForDenseRank(overParam));
    }

    @Override
    public JDFrameImpl<T> overDenseRankS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((JDFrameImpl)this.overDenseRank((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public JDFrameImpl<FI2<T, BigDecimal>> overPercentRank(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForPercentRank(overParam));
    }

    @Override
    public JDFrameImpl<T> overPercentRankS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam) {
        return this.fi2Frame((JDFrameImpl)this.overPercentRank((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public JDFrameImpl<FI2<T, BigDecimal>> overCumeDist(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForCumeDist(overParam));
    }

    @Override
    public JDFrameImpl<T> overCumeDistS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam) {
        return this.fi2Frame((JDFrameImpl)this.overCumeDist((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLag(Window<T> overParam, Function<T, F> field, int n) {
        return this.returnDF(this.windowFunctionForLag(overParam, field, n));
    }

    @Override
    public <F> JDFrameImpl<T> overLagS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field, int n) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overLag((Window)overParam, (Function)field, n), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLag(Function<T, F> field, int n) {
        return this.overLag(this.emptyWindow, (Function)field, n);
    }

    @Override
    public <F> JDFrameImpl<T> overLagS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overLag((Function)field, n), setFunction);
    }

    @Override
    public <F> JDFrameImpl<T> overLeadS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field, int n) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overLead((Window)overParam, (Function)field, n), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLead(Window<T> overParam, Function<T, F> field, int n) {
        return this.returnDF(this.windowFunctionForLead(overParam, field, n));
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLead(Function<T, F> field, int n) {
        return this.overLead(this.emptyWindow, (Function)field, n);
    }

    @Override
    public <F> JDFrameImpl<T> overLeadS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overLead((Function)field, n), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overNthValue(Window<T> overParam, Function<T, F> field, int n) {
        return this.returnDF(this.windowFunctionForNthValue(overParam, field, n));
    }

    @Override
    public <F> JDFrameImpl<T> overNthValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field, int n) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overNthValue((Window)overParam, (Function)field, n), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overNthValue(Function<T, F> field, int n) {
        return this.overNthValue(this.emptyWindow, (Function)field, n);
    }

    @Override
    public <F> JDFrameImpl<T> overNthValueS(SetFunction<T, F> setFunction, Function<T, F> field, int n) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overNthValue((Function)field, n), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overFirstValue(Window<T> overParam, Function<T, F> field) {
        return this.overNthValue((Window)overParam, (Function)field, 1);
    }

    @Override
    public <F> JDFrameImpl<T> overFirstValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overFirstValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overFirstValue(Function<T, F> field) {
        return this.overFirstValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> JDFrameImpl<T> overFirstValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overFirstValue((Function)field), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLastValue(Window<T> overParam, Function<T, F> field) {
        return this.overNthValue((Window)overParam, (Function)field, -1);
    }

    @Override
    public <F> JDFrameImpl<T> overLastValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overLastValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, F>> overLastValue(Function<T, F> field) {
        return this.overLastValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> JDFrameImpl<T> overLastValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overLastValue((Function)field), setFunction);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overSum(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForSum(overParam, field));
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overSum(Function<T, F> field) {
        return this.overSum(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> JDFrameImpl<T> overSumS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overSum((Window)overParam, (Function)field), (SetFunction<T, F>)setFunction);
    }

    @Override
    public <F> JDFrameImpl<T> overSumS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.overSumS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overAvg(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForAvg(overParam, field));
    }

    @Override
    public <F> JDFrameImpl<FI2<T, BigDecimal>> overAvg(Function<T, F> field) {
        return this.overAvg(this.emptyWindow, (Function)field);
    }

    @Override
    public <F> JDFrameImpl<T> overAvgS(SetFunction<T, BigDecimal> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overAvg((Window)overParam, (Function)field), (SetFunction<T, F>)setFunction);
    }

    @Override
    public <F> JDFrameImpl<T> overAvgS(SetFunction<T, BigDecimal> setFunction, Function<T, F> field) {
        return this.overAvgS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMaxValue(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForMaxValue(overParam, field));
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMaxValue(Function<T, F> field) {
        return this.overMaxValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<T> overMaxValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overMaxValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<T> overMaxValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.overMaxValueS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMinValue(Window<T> overParam, Function<T, F> field) {
        return this.returnDF(this.windowFunctionForMinValue(overParam, field));
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<FI2<T, F>> overMinValue(Function<T, F> field) {
        return this.overMinValue(this.emptyWindow, (Function)field);
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<T> overMinValueS(SetFunction<T, F> setFunction, Window<T> overParam, Function<T, F> field) {
        return this.fi2Frame((JDFrameImpl<FI2<T, F>>)this.overMinValue((Window)overParam, (Function)field), setFunction);
    }

    @Override
    public <F extends Comparable<? super F>> JDFrameImpl<T> overMinValueS(SetFunction<T, F> setFunction, Function<T, F> field) {
        return this.overMinValueS((SetFunction)setFunction, this.emptyWindow, (Function)field);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overCount(Window<T> overParam) {
        return this.returnDF(this.windowFunctionForCount(overParam));
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overCount() {
        return this.overCount(this.emptyWindow);
    }

    @Override
    public JDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction, Window<T> overParam) {
        return this.fi2Frame((JDFrameImpl)this.overCount((Window)overParam), (SetFunction)setFunction);
    }

    @Override
    public JDFrameImpl<T> overCountS(SetFunction<T, Integer> setFunction) {
        return this.overCountS((SetFunction)setFunction, this.emptyWindow);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overNtile(int n) {
        return this.overNtile(this.emptyWindow, n);
    }

    @Override
    public JDFrameImpl<FI2<T, Integer>> overNtile(Window<T> overParam, int n) {
        return this.returnDF(this.windowFunctionForNtile(overParam, n));
    }

    @Override
    public JDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, Window<T> overParam, int n) {
        return this.fi2Frame((JDFrameImpl)this.overNtile((Window)overParam, n), (SetFunction)setFunction);
    }

    @Override
    public JDFrameImpl<T> overNtileS(SetFunction<T, Integer> setFunction, int n) {
        return this.overNtileS((SetFunction)setFunction, this.emptyWindow, n);
    }

    @Override
    public JDFrameImpl<T> unionAll(IFrame<T> other) {
        ArrayList<T> ts = new ArrayList<T>(this.viewList());
        ts.addAll(other.toLists());
        return this.returnDF(ts);
    }

    @Override
    public JDFrameImpl<T> unionAll(Collection<T> other) {
        ArrayList<T> ts = new ArrayList<T>(this.viewList());
        ts.addAll(other);
        return this.returnDF(ts);
    }

    @Override
    public JDFrameImpl<T> union(IFrame<T> other) {
        return this.returnDF(this.unionList(this.viewList(), other.toLists()));
    }

    @Override
    public JDFrameImpl<T> union(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.unionList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public JDFrameImpl<T> union(Collection<T> other) {
        return this.returnDF(this.unionList(this.viewList(), other));
    }

    @Override
    public JDFrameImpl<T> union(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.unionList(this.viewList(), other, comparator));
    }

    @Override
    public JDFrameImpl<T> retainAll(IFrame<T> other) {
        return this.returnDF(this.retainAllList(this.viewList(), other.toLists()));
    }

    @Override
    public JDFrameImpl<T> retainAll(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.retainAllList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public JDFrameImpl<T> retainAll(Collection<T> other) {
        return this.returnDF(this.retainAllList(this.viewList(), other));
    }

    @Override
    public JDFrameImpl<T> retainAll(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.retainAllList(this.viewList(), other, comparator));
    }

    @Override
    public JDFrameImpl<T> intersection(IFrame<T> other) {
        return this.returnDF(this.intersectionList(this.viewList(), other.toLists()));
    }

    @Override
    public JDFrame<T> intersection(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.intersectionList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public JDFrameImpl<T> intersection(Collection<T> other) {
        return this.returnDF(this.intersectionList(this.viewList(), other));
    }

    @Override
    public JDFrameImpl<T> intersection(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.intersectionList(this.viewList(), other, comparator));
    }

    @Override
    public JDFrameImpl<T> different(IFrame<T> other) {
        return this.returnDF(this.differentList(this.viewList(), other.toLists()));
    }

    @Override
    public JDFrameImpl<T> different(IFrame<T> other, Comparator<T> comparator) {
        return this.returnDF(this.differentList(this.viewList(), other.toLists(), comparator));
    }

    @Override
    public JDFrameImpl<T> different(Collection<T> other) {
        return this.returnDF(this.differentList(this.viewList(), other));
    }

    @Override
    public JDFrame<T> different(Collection<T> other, Comparator<T> comparator) {
        return this.returnDF(this.differentList(this.viewList(), other, comparator));
    }

    @Override
    public <G, C> JDFrameImpl<T> replenish(Function<T, G> groupDim, Function<T, C> collectDim, List<C> allDim, ReplenishFunction<G, C, T> getEmptyObject) {
        return this.returnDF(JDFrameImpl.replenish(this.viewList(), groupDim, collectDim, allDim, getEmptyObject));
    }

    @Override
    public <C> JDFrameImpl<T> replenish(Function<T, C> collectDim, List<C> allDim, Function<C, T> getEmptyObject) {
        return this.returnDF(JDFrameImpl.replenish(this.viewList(), collectDim, allDim, getEmptyObject));
    }

    @Override
    public <G, C> JDFrameImpl<T> replenish(Function<T, G> groupDim, Function<T, C> collectDim, ReplenishFunction<G, C, T> getEmptyObject) {
        return this.returnDF(JDFrameImpl.replenish(this.viewList(), groupDim, collectDim, getEmptyObject));
    }

    protected <R> JDFrameImpl<R> returnDF(Stream<R> stream) {
        JDFrameImpl frame = new JDFrameImpl(stream.collect(Collectors.toList()));
        this.transmitMember(this, frame);
        return frame;
    }

    protected <R> JDFrameImpl<R> returnDF(List<R> dataList) {
        JDFrameImpl<R> frame = new JDFrameImpl<R>(dataList);
        this.transmitMember(this, frame);
        return frame;
    }
}

