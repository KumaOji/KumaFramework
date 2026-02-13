/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONArray
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.ListToOneFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.ReplenishFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.impl.AbstractWindowDataFrame;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.item.FI4;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.MaxMin;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;
import com.kuma.boot.common.support.dataframe.util.BeanCopyUtil;
import com.kuma.boot.common.support.dataframe.util.CollectorsPlusUtil;
import com.kuma.boot.common.support.dataframe.util.FrameUtil;
import com.kuma.boot.common.support.dataframe.util.ListUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDataFrameImpl<T>
extends AbstractWindowDataFrame<T> {
    protected AbstractDataFrameImpl() {
    }

    @Override
    public T[] toArray() {
        List ts = this.viewList();
        if (ts.isEmpty() && this.fieldClass == null) {
            return null;
        }
        Object[] arr = (Object[])Array.newInstance(this.fieldClass, ts.size());
        for (int i = 0; i < ts.size(); ++i) {
            arr[i] = ts.get(i);
        }
        return arr;
    }

    @Override
    public T[] toArray(Class<T> elementClass) {
        List ts = this.viewList();
        if (ts == null || ts.isEmpty()) {
            return (Object[])Array.newInstance(elementClass, 0);
        }
        Object[] array = (Object[])Array.newInstance(elementClass, ts.size());
        for (int i = 0; i < ts.size(); ++i) {
            array[i] = ts.get(i);
        }
        return array;
    }

    @Override
    public boolean contains(T other) {
        return this.viewList().contains(other);
    }

    @Override
    public <U> boolean containsValue(Function<T, U> valueFunction, U value) {
        return this.stream().anyMatch(e -> {
            if (e == null) {
                return false;
            }
            Object fieldValue = valueFunction.apply(e);
            if (fieldValue == null && value == null) {
                return true;
            }
            if (value != null) {
                return value.equals(fieldValue);
            }
            return false;
        });
    }

    @Override
    public <U> String joining(Function<T, U> joinField, CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return this.stream().map(joinField).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(delimiter, prefix, suffix));
    }

    @Override
    public <U> String joining(Function<T, U> joinField, CharSequence delimiter) {
        return this.joining(joinField, delimiter, "", "");
    }

    @Override
    public <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        List list = this.viewList();
        if (ListUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        HashMap<K, V> map = new HashMap<K, V>(list.size());
        for (Object t : list) {
            map.put(keyMapper.apply(t), valueMapper.apply(t));
        }
        return map;
    }

    @Override
    public <K, K2, V> Map<K, Map<K2, V>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends K2> key2Mapper, Function<? super T, ? extends V> valueMapper) {
        Map<Object, List<T>> oldMap = this.stream().collect(Collectors.groupingBy(keyMapper));
        HashMap map = new HashMap(oldMap.size());
        oldMap.forEach((? super K key, ? super V list) -> map.put(key, this.from(list.stream()).toMap(key2Mapper, valueMapper)));
        return map;
    }

    protected <R> Stream<T> whereNullStream(Function<T, R> function) {
        return this.stream().filter(item -> {
            Object r = function.apply(item);
            if (r == null) {
                return true;
            }
            if (r instanceof String) {
                return "".equals(r);
            }
            return false;
        });
    }

    protected <R> Stream<T> whereNotNullStream(Function<T, R> function) {
        return this.stream().filter(item -> {
            Object r = function.apply(item);
            if (r == null) {
                return false;
            }
            if (r instanceof String) {
                return !"".equals(r);
            }
            return true;
        });
    }

    public <R extends Comparable<R>> Stream<T> whereBetweenStream(Function<T, R> function, R start, R end) {
        Stream<Object> stream = this.streamFilterNull(function);
        stream = start == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(end) <= 0) : (end == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) >= 0) : stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) >= 0 && ((Comparable)function.apply(e)).compareTo(end) <= 0));
        return stream;
    }

    public <R extends Comparable<R>> Stream<T> whereBetweenNStream(Function<T, R> function, R start, R end) {
        Stream<Object> stream = this.streamFilterNull(function);
        stream = start == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(end) < 0) : (end == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) > 0) : stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) > 0 && ((Comparable)function.apply(e)).compareTo(end) < 0));
        return stream;
    }

    public <R extends Comparable<R>> Stream<T> whereBetweenRStream(Function<T, R> function, R start, R end) {
        Stream<Object> stream = this.streamFilterNull(function);
        stream = start == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(end) <= 0) : (end == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) > 0) : stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) > 0 && ((Comparable)function.apply(e)).compareTo(end) <= 0));
        return stream;
    }

    public <R extends Comparable<R>> Stream<T> whereBetweenLStream(Function<T, R> function, R start, R end) {
        Stream<Object> stream = this.streamFilterNull(function);
        stream = start == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(end) < 0) : (end == null ? stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) >= 0) : stream.filter(e -> ((Comparable)function.apply(e)).compareTo(start) >= 0 && ((Comparable)function.apply(e)).compareTo(end) < 0));
        return stream;
    }

    public <R extends Comparable<R>> Stream<T> whereNotBetweenStream(Function<T, R> function, R start, R end) {
        return this.streamFilterNull(function).filter(e -> ((Comparable)function.apply(e)).compareTo(start) <= 0 || ((Comparable)function.apply(e)).compareTo(end) >= 0);
    }

    public <R extends Comparable<R>> Stream<T> whereNotBetweenNStream(Function<T, R> function, R start, R end) {
        return this.streamFilterNull(function).filter(e -> ((Comparable)function.apply(e)).compareTo(start) < 0 || ((Comparable)function.apply(e)).compareTo(end) > 0);
    }

    public <R> Stream<T> whereInStream(Function<T, R> function, List<R> list) {
        HashSet set = new HashSet(list);
        return this.stream().filter(e -> set.contains(function.apply(e)));
    }

    public <R> Stream<T> whereNotInStream(Function<T, R> function, List<R> list) {
        HashSet set = new HashSet(list);
        return this.stream().filter(e -> !set.contains(function.apply(e)));
    }

    public <R> Stream<T> whereEqStream(Function<T, R> function, R value) {
        return this.stream().filter(e -> {
            if (e == null) {
                return false;
            }
            Object fieldValue = function.apply(e);
            if (fieldValue == null && value == null) {
                return true;
            }
            if (value != null) {
                return value.equals(fieldValue);
            }
            return false;
        });
    }

    public <R> Stream<T> whereNotEqStream(Function<T, R> function, R value) {
        return this.stream().filter(e -> !value.equals(function.apply(e)));
    }

    public <R extends Comparable<R>> Stream<T> whereGtStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> ((Comparable)function.apply(e)).compareTo(value) > 0);
    }

    public <R extends Comparable<R>> Stream<T> whereGeStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> ((Comparable)function.apply(e)).compareTo(value) >= 0);
    }

    public <R extends Comparable<R>> Stream<T> whereLtStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> ((Comparable)function.apply(e)).compareTo(value) < 0);
    }

    public <R extends Comparable<R>> Stream<T> whereLeStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> ((Comparable)function.apply(e)).compareTo(value) <= 0);
    }

    public <R> Stream<T> whereLikeStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> String.valueOf(function.apply(e)).contains(String.valueOf(value)));
    }

    public <R> Stream<T> whereNotLikeStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> !String.valueOf(function.apply(e)).contains(String.valueOf(value)));
    }

    public <R> Stream<T> whereLikeLeftStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> String.valueOf(function.apply(e)).startsWith(String.valueOf(value)));
    }

    public <R> Stream<T> whereLikeRightStream(Function<T, R> function, R value) {
        return this.streamFilterNull(function).filter(e -> String.valueOf(function.apply(e)).endsWith(String.valueOf(value)));
    }

    @Override
    public <R> BigDecimal sum(Function<T, R> function) {
        return this.stream().map(function).filter(Objects::nonNull).collect(CollectorsPlusUtil.summingBigDecimal(e -> {
            if (e instanceof BigDecimal) {
                return (BigDecimal)e;
            }
            return new BigDecimal(String.valueOf(e));
        }));
    }

    @Override
    public <R> BigDecimal avg(Function<T, R> function) {
        List bigDecimalList = this.stream().map(function).filter(Objects::nonNull).map((? super T e) -> {
            if (e instanceof BigDecimal) {
                return (BigDecimal)e;
            }
            return new BigDecimal(String.valueOf(e));
        }).collect(Collectors.toList());
        if (bigDecimalList.isEmpty()) {
            return null;
        }
        return bigDecimalList.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(bigDecimalList.size()), this.defaultScale, this.defaultRoundingMode);
    }

    @Override
    public <R extends Comparable<? super R>> MaxMin<R> maxMinValue(Function<T, R> function) {
        MaxMin<T> maxAndMin = this.maxMin(function);
        return new MaxMin<Comparable>((Comparable)this.getApplyValue(function, maxAndMin.getMax()), (Comparable)this.getApplyValue(function, maxAndMin.getMin()));
    }

    @Override
    public <R extends Comparable<? super R>> MaxMin<T> maxMin(Function<T, R> function) {
        List itemList = this.stream().filter(e -> e != null && function.apply(e) != null).collect(Collectors.toList());
        if (itemList.isEmpty()) {
            return new MaxMin<Object>(null, null);
        }
        Object max = itemList.get(0);
        Object min = itemList.get(0);
        for (int i = 1; i < itemList.size(); ++i) {
            Object cur = itemList.get(i);
            Comparable curValue = (Comparable)function.apply(cur);
            Comparable maxValue = (Comparable)function.apply(max);
            Comparable minValue = (Comparable)function.apply(min);
            if (curValue.compareTo(maxValue) >= 0) {
                max = cur;
            }
            if (curValue.compareTo(minValue) > 0) continue;
            min = cur;
        }
        return new MaxMin(max, min);
    }

    @Override
    public <R extends Comparable<? super R>> R maxValue(Function<T, R> function) {
        Optional<Comparable> value = this.stream().map(function).filter(Objects::nonNull).max(Comparator.comparing(e -> e));
        return (R)((Comparable)value.orElse(null));
    }

    @Override
    public <R extends Comparable<R>> T max(Function<T, R> function) {
        Optional<Object> max = this.stream().filter(e -> function.apply(e) != null).max(Comparator.comparing(function));
        return max.orElse(null);
    }

    @Override
    public <R extends Comparable<? super R>> R minValue(Function<T, R> function) {
        Optional<Comparable> value = this.stream().map(function).filter(Objects::nonNull).min(Comparator.comparing(e -> e));
        return (R)((Comparable)value.orElse(null));
    }

    @Override
    public <R extends Comparable<R>> T min(Function<T, R> function) {
        Optional<Object> min = this.stream().filter(e -> function.apply(e) != null).min(Comparator.comparing(function));
        return min.orElse(null);
    }

    @Override
    public long count() {
        return this.stream().count();
    }

    @Override
    public boolean isEmpty() {
        return this.count() <= 0L;
    }

    @Override
    public boolean isNotEmpty() {
        return this.count() > 0L;
    }

    protected <K> List<FI2<K, List<T>>> groupKey(Function<? super T, ? extends K> K) {
        return FrameUtil.toListFI2(this.stream().collect(Collectors.groupingBy(K)));
    }

    protected <K, V> List<FI2<K, V>> groupKey(Function<T, K> K, Collector<T, ?, V> tBigDecimalCollector) {
        Map<K, V> resultMap = this.stream().collect(Collectors.groupingBy(K, tBigDecimalCollector));
        return FrameUtil.toListFI2(resultMap);
    }

    protected <K, J, V> List<FI3<K, J, V>> groupKey(Function<T, K> K, Function<T, J> J, Collector<T, ?, V> tBigDecimalCollector) {
        Map<K, Map<J, V>> map = this.stream().collect(Collectors.groupingBy(K, Collectors.groupingBy(J, tBigDecimalCollector)));
        return FrameUtil.toListFI3(map);
    }

    protected <K, J, H, V> List<FI4<K, J, H, V>> groupKey(Function<T, K> K, Function<T, J> J, Function<T, H> H, Collector<T, ?, V> collectorType) {
        Map<K, Map<J, Map<H, V>>> map = this.stream().collect(Collectors.groupingBy(K, Collectors.groupingBy(J, Collectors.groupingBy(H, collectorType))));
        return FrameUtil.toListFI4(map);
    }

    protected <K, J, V extends Comparable<V>> Map<K, Map<J, T>> groupToMap(Function<T, K> key, Function<T, J> key2, Function<List<T>, T> getListMaxFunction) {
        return this.stream().collect(Collectors.groupingBy(key, Collectors.groupingBy(key2, Collectors.collectingAndThen(Collectors.toList(), getListMaxFunction))));
    }

    protected <V extends Comparable<? super V>> Function<List<T>, T> getListMaxFunction(Function<T, V> value) {
        return e -> e.stream().filter(a -> value.apply(a) != null).max(Comparator.comparing(value)).orElse(null);
    }

    protected <V extends Comparable<? super V>> Function<List<T>, T> getListMinFunction(Function<T, V> value) {
        return e -> e.stream().min(Comparator.comparing(value)).orElse(null);
    }

    protected <V extends Comparable<? super V>> Function<List<T>, MaxMin<V>> getListGroupMaxMinValueFunction(Function<T, V> value) {
        return list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            MaxMin<Comparable> maxMin = new MaxMin<Comparable>();
            maxMin.setMax(list.stream().max(Comparator.comparing(value)).map(value).orElse(null));
            maxMin.setMin(list.stream().min(Comparator.comparing(value)).map(value).orElse(null));
            return maxMin;
        };
    }

    protected <V extends Comparable<? super V>> Function<List<T>, MaxMin<T>> getListGroupMaxMinFunction(Function<T, V> value) {
        return list -> {
            if (list == null || list.isEmpty()) {
                return new MaxMin();
            }
            MaxMin<Object> maxMin = new MaxMin<Object>();
            maxMin.setMax(list.stream().max(Comparator.comparing(value)).orElse(null));
            maxMin.setMin(list.stream().min(Comparator.comparing(value)).orElse(null));
            return maxMin;
        };
    }

    public <R> Stream<T> streamFilterNull(Function<T, R> function) {
        return this.stream().filter(e -> e != null && function.apply(e) != null);
    }

    @Override
    public Iterator<T> iterator() {
        return this.viewList().iterator();
    }

    @Override
    public List<String> columns() {
        return this.getFieldList();
    }

    @Override
    public <R> List<R> col(Function<T, R> function) {
        return this.viewList().stream().map(function).collect(Collectors.toList());
    }

    @Override
    public List<T> page(int page, int pageSize) {
        int count;
        int startIndex;
        if (page < 0 || pageSize < 1) {
            throw new IllegalArgumentException("Page and pageSize must be positive integers.");
        }
        if (page == 0) {
            page = 1;
        }
        if ((startIndex = (page - 1) * pageSize) >= (count = (int)this.count())) {
            return Collections.emptyList();
        }
        int endIndex = Math.min(startIndex + pageSize, count);
        return this.viewList().subList(startIndex, endIndex);
    }

    public String toString() {
        return this.getShowString(15).toString();
    }

    @Override
    public void show() {
        this.show(15);
    }

    @Override
    public void show(int n) {
        StringBuilder sb = this.getShowString(n);
        System.out.println(sb);
    }

    protected List<T> distinctList(List<T> dataList, Comparator<T> comparator, ListToOneFunction<T> function) {
        if (ListUtils.isEmpty(dataList) || dataList.size() == 1) {
            return dataList;
        }
        TreeMap treeMap = new TreeMap(comparator);
        for (T t : dataList) {
            treeMap.putIfAbsent(t, new ArrayList());
            List tmpList = (List)treeMap.get(t);
            tmpList.add(t);
        }
        return treeMap.values().stream().map((? super T list) -> {
            if (list.size() == 1) {
                return list.get(0);
            }
            return function.apply((List)list);
        }).collect(Collectors.toList());
    }

    protected <R, K> List<R> joinList(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.joinList(other, on, join, false);
    }

    protected <R, K> List<R> joinList(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join, boolean isJoinOnce) {
        ArrayList<R> resultList = new ArrayList<R>();
        block0: for (T cur : this) {
            for (Object k : other) {
                if (!on.on(cur, k)) continue;
                resultList.add(join.join(cur, k));
                if (!isJoinOnce) continue;
                continue block0;
            }
        }
        return resultList;
    }

    protected <K> void joinListLink(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.joinListLink(other, on, join, false);
    }

    protected <K> void joinListLink(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join, boolean isJoinOnce) {
        block0: for (T cur : this) {
            for (Object k : other) {
                if (!on.on(cur, k)) continue;
                join.join(cur, k);
                if (!isJoinOnce) continue;
                continue block0;
            }
        }
    }

    protected <R, K> List<R> leftJoinList(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.leftJoinList(other, on, join, false);
    }

    protected <R, K> List<R> leftJoinList(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join, boolean isJoinOnce) {
        ArrayList<R> resultList = new ArrayList<R>();
        block0: for (T cur : this) {
            for (Object k : other) {
                if (on.on(cur, k)) {
                    resultList.add(join.join(cur, k));
                    if (!isJoinOnce) continue;
                    continue block0;
                }
                resultList.add(join.join(cur, null));
            }
        }
        return resultList;
    }

    protected <K> void leftJoinListLink(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.leftJoinListLink(other, on, join, false);
    }

    protected <K> void leftJoinListLink(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join, boolean isJoinOnce) {
        block0: for (T cur : this) {
            for (Object k : other) {
                if (on.on(cur, k)) {
                    join.join(cur, k);
                    if (!isJoinOnce) continue;
                    continue block0;
                }
                join.join(cur, null);
            }
        }
    }

    protected <R, K> List<R> rightJoinList(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join) {
        return this.rightJoinList(other, on, join, false);
    }

    protected <R, K> List<R> rightJoinList(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join, boolean isJoinOnce) {
        ArrayList<R> resultList = new ArrayList<R>();
        block0: for (Object k : other) {
            for (T cur : this) {
                if (on.on(cur, k)) {
                    resultList.add(join.join(cur, k));
                    if (!isJoinOnce) continue;
                    continue block0;
                }
                resultList.add(join.join(null, k));
            }
        }
        return resultList;
    }

    protected <K> void rightJoinListLink(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join) {
        this.rightJoinListLink(other, on, join, false);
    }

    protected <K> void rightJoinListLink(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join, boolean isJoinOnce) {
        block0: for (Object k : other) {
            for (T cur : this) {
                if (on.on(cur, k)) {
                    join.join(cur, k);
                    if (!isJoinOnce) continue;
                    continue block0;
                }
                join.join(null, k);
            }
        }
    }

    @Override
    public List<T> head(int n) {
        List tsList = this.viewList();
        if (tsList.isEmpty()) {
            return Collections.emptyList();
        }
        if (n >= tsList.size()) {
            return tsList;
        }
        return tsList.subList(0, n);
    }

    @Override
    public List<T> tail(int n) {
        List tsList = this.viewList();
        if (tsList.isEmpty()) {
            return Collections.emptyList();
        }
        if (n >= tsList.size()) {
            return tsList;
        }
        return tsList.subList(tsList.size() - 1 - n + 1, tsList.size());
    }

    @Override
    public T head() {
        List ts = this.viewList();
        return ts.isEmpty() ? null : (T)ts.get(0);
    }

    @Override
    public T tail() {
        List ts = this.viewList();
        return ts.isEmpty() ? null : (T)ts.get(ts.size() - 1);
    }

    @Override
    public List<T> getList(Integer startIndex, Integer endIndex) {
        List ts = this.viewList();
        if (startIndex == null || startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex == null || endIndex > ts.size()) {
            endIndex = ts.size();
        }
        return ts.subList(startIndex, endIndex);
    }

    protected List<T> unionList(List<T> leftList, Collection<T> rightList) {
        if (ListUtils.isEmpty(rightList)) {
            return leftList;
        }
        if (ListUtils.isEmpty(leftList)) {
            return new ArrayList<T>(rightList);
        }
        HashSet<T> set = new HashSet<T>(leftList);
        set.addAll(rightList);
        return new ArrayList<T>(set);
    }

    protected List<T> unionList(List<T> leftList, Collection<T> rightList, Comparator<T> comparator) {
        if (ListUtils.isEmpty(rightList)) {
            return leftList;
        }
        if (ListUtils.isEmpty(leftList)) {
            return new ArrayList<T>(rightList);
        }
        TreeSet<T> set = new TreeSet<T>(comparator);
        set.addAll(leftList);
        set.addAll(rightList);
        return new ArrayList<T>(set);
    }

    protected List<T> retainAllList(List<T> leftList, Collection<T> rightList) {
        if (ListUtils.isEmpty(rightList)) {
            return Collections.emptyList();
        }
        HashSet<T> set = new HashSet<T>(rightList);
        return leftList.stream().filter(set::contains).collect(Collectors.toList());
    }

    protected List<T> retainAllList(List<T> leftList, Collection<T> rightList, Comparator<T> comparator) {
        if (ListUtils.isEmpty(rightList)) {
            return Collections.emptyList();
        }
        TreeSet<T> set = new TreeSet<T>(comparator);
        set.addAll(rightList);
        return leftList.stream().filter(set::contains).collect(Collectors.toList());
    }

    protected List<T> intersectionList(List<T> leftList, Collection<T> rightList) {
        if (ListUtils.isEmpty(leftList) || ListUtils.isEmpty(rightList)) {
            return Collections.emptyList();
        }
        HashSet<T> set = new HashSet<T>(rightList);
        return leftList.stream().filter(set::contains).distinct().collect(Collectors.toList());
    }

    protected List<T> intersectionList(List<T> leftList, Collection<T> rightList, Comparator<T> comparator) {
        if (ListUtils.isEmpty(leftList) || ListUtils.isEmpty(rightList)) {
            return Collections.emptyList();
        }
        TreeSet<T> set = new TreeSet<T>(comparator);
        set.addAll(rightList);
        return leftList.stream().filter(set::contains).distinct().collect(Collectors.toList());
    }

    protected List<T> differentList(List<T> leftList, Collection<T> rightList) {
        if (ListUtils.isEmpty(leftList)) {
            return leftList;
        }
        if (ListUtils.isEmpty(rightList)) {
            return leftList;
        }
        HashSet otherSet = new HashSet(rightList);
        leftList = leftList.stream().filter(e -> !otherSet.contains(e)).collect(Collectors.toList());
        return leftList;
    }

    protected List<T> differentList(List<T> leftList, Collection<T> rightList, Comparator<T> comparator) {
        if (ListUtils.isEmpty(leftList)) {
            return leftList;
        }
        if (ListUtils.isEmpty(rightList)) {
            return leftList;
        }
        TreeSet otherSet = new TreeSet(comparator);
        otherSet.addAll(rightList);
        leftList = leftList.stream().filter(e -> !otherSet.contains(e)).collect(Collectors.toList());
        return leftList;
    }

    protected static <T, C> List<T> replenish(List<T> itemDTOList, Function<T, C> collectDim, List<C> allDim, Function<C, T> getEmptyObject) {
        allDim = new ArrayList<C>(new HashSet<C>(allDim));
        List collect = itemDTOList.stream().map(collectDim).collect(Collectors.toList());
        collect = new ArrayList(new HashSet(collect));
        allDim.removeAll(collect);
        List collect1 = allDim.stream().map(getEmptyObject).collect(Collectors.toList());
        itemDTOList.addAll(collect1);
        return itemDTOList;
    }

    public static <T, G, C> List<T> replenish(List<T> itemDTOList, Function<T, G> groupDim, Function<T, C> collectDim, List<C> allDim, ReplenishFunction<G, C, T> getEmptyObject) {
        Map<G, List<T>> nameItemListMap = itemDTOList.stream().collect(Collectors.groupingBy(groupDim));
        nameItemListMap.forEach((? super K name, ? super V itemList) -> {
            ArrayList tmpAll = new ArrayList(allDim);
            List abasicssaList = itemList.stream().map(collectDim).collect(Collectors.toList());
            tmpAll.removeAll(abasicssaList);
            if (ListUtils.isNotEmpty(tmpAll)) {
                List missingList = tmpAll.stream().map((? super T e) -> getEmptyObject.apply(name, e)).collect(Collectors.toList());
                itemList.addAll(missingList);
            }
        });
        return nameItemListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    protected static <T, G, C> List<T> replenish(List<T> itemDTOList, Function<T, G> groupDim, Function<T, C> collectDim, ReplenishFunction<G, C, T> getEmptyObject) {
        List allDim = itemDTOList.stream().map(collectDim).filter(Objects::nonNull).collect(Collectors.toList());
        allDim = new ArrayList(new HashSet(allDim));
        return AbstractDataFrameImpl.replenish(itemDTOList, groupDim, collectDim, allDim, getEmptyObject);
    }

    protected static <C> List<C> mergeCollection(Collection<List<C>> values) {
        List allAbscissa = values.stream().flatMap(Collection::stream).collect(Collectors.toList());
        allAbscissa = new HashSet(allAbscissa).stream().collect(Collectors.toList());
        return allAbscissa;
    }

    protected <R> R getApplyValue(Function<T, R> fun, T obj) {
        return obj == null ? null : (R)fun.apply(obj);
    }

    protected <F> Stream<T> fi2Stream(Stream<FI2<T, F>> stream, SetFunction<T, F> setFunction) {
        return stream.map((? super T e) -> {
            setFunction.accept(e.getC1(), e.getC2());
            return e.getC1();
        });
    }

    protected Stream<FI2<T, String>> explodeStringStream(Function<T, String> getFunction, String delimiter) {
        return this.stream().flatMap(e -> {
            String fieldValue = (String)getFunction.apply(e);
            if (StringUtils.isBlank(fieldValue)) {
                return Stream.of(new FI2<Object, String>(e, fieldValue));
            }
            fieldValue = fieldValue.trim();
            String[] arrText = (fieldValue = StringUtils.strip((CharSequence)fieldValue, (CharSequence)"[]")).split(delimiter);
            int length = arrText.length;
            if (length <= 1) {
                return Stream.of(new FI2<Object, String>(e, (String)getFunction.apply(e)));
            }
            return Arrays.stream(arrText).map((? super T text) -> new FI2(BeanCopyUtil.copyProperties(e, e.getClass()), StringUtils.strip((CharSequence)text, (CharSequence)"\""))).collect(Collectors.toList()).stream();
        });
    }

    protected Stream<FI2<T, String>> explodeJsonArrayStream(Function<T, String> getFunction) {
        return this.stream().flatMap(e -> {
            String fieldValue = (String)getFunction.apply(e);
            if (StringUtils.isBlank(fieldValue) || !JSON.isValidArray((String)fieldValue)) {
                return Stream.of(new FI2<Object, String>(e, fieldValue));
            }
            JSONArray objects = JSON.parseArray((String)fieldValue);
            if (objects.isEmpty()) {
                return Stream.of(new FI2<Object, String>(e, fieldValue));
            }
            if (objects.size() == 1) {
                return Stream.of(new FI2<Object, String>(e, objects.get(0).toString()));
            }
            return objects.stream().map((? super T text) -> new FI2(BeanCopyUtil.copyProperties(e, e.getClass()), text.toString())).collect(Collectors.toList()).stream();
        });
    }

    protected <E> Stream<FI2<T, E>> explodeCollectionStream(Function<T, ? extends Collection<E>> getFunction) {
        return this.stream().flatMap(e -> {
            Object fieldValue = getFunction.apply(e);
            if (fieldValue == null) {
                return Stream.of(new FI2<Object, Object>(e, null));
            }
            Class<?> fieldValueClass = fieldValue.getClass();
            if (!Collection.class.isAssignableFrom(fieldValueClass)) {
                return Stream.of(new FI2<Object, Object>(e, null));
            }
            Collection objects = (Collection)fieldValue;
            if (objects.isEmpty()) {
                return Stream.of(new FI2<Object, Object>(e, null));
            }
            if (objects.size() == 1) {
                return Stream.of(new FI2(e, objects.iterator().next()));
            }
            return objects.stream().map((? super T text) -> new FI2(BeanCopyUtil.copyProperties(e, e.getClass()), text)).collect(Collectors.toList()).stream();
        });
    }

    protected <E> Stream<FI2<T, E>> explodeCollectionArrayStream(Function<T, ?> getFunction, Class<E> elementClass) {
        return this.stream().flatMap(e -> {
            Object fieldValue = getFunction.apply(e);
            if (fieldValue == null) {
                return Stream.of(new FI2<Object, Object>(e, null));
            }
            Class<?> fieldValueClass = fieldValue.getClass();
            if (!fieldValueClass.isArray() && !Collection.class.isAssignableFrom(fieldValueClass)) {
                return Stream.of(new FI2<Object, Object>(e, null));
            }
            Stream<Object> stream = null;
            if (fieldValueClass.isArray()) {
                Object[] objects = (Object[])fieldValue;
                stream = Arrays.stream(objects);
                if (objects.length == 0) {
                    return Stream.of(new FI2<Object, Object>(e, null));
                }
                if (objects.length == 1) {
                    return Stream.of(new FI2(e, elementClass.cast(objects[0])));
                }
            } else if (Collection.class.isAssignableFrom(fieldValueClass)) {
                Collection objects = (Collection)fieldValue;
                if (objects.isEmpty()) {
                    return Stream.of(new FI2<Object, Object>(e, null));
                }
                if (objects.size() == 1) {
                    return Stream.of(new FI2(e, elementClass.cast(objects.iterator().next())));
                }
                stream = objects.stream();
            }
            return stream.map((? super T text) -> new FI2(BeanCopyUtil.copyProperties(e, e.getClass()), elementClass.cast(text))).collect(Collectors.toList()).stream();
        });
    }
}

