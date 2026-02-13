/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.bean.BeanUtil
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.text.CharSequenceUtil
 */
package com.kuma.boot.common.utils.lambda;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.kuma.boot.common.utils.lambda.LambdaBuilder;
import com.kuma.boot.common.utils.lambda.SFunction;
import com.kuma.boot.common.utils.lambda.XmMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LamUtils {
    public static String join(List<String> originList, String delimiter) {
        if (CollUtil.isEmpty(originList)) {
            return null;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return null;
        }
        return originList.stream().filter(CharSequenceUtil::isNotBlank).collect(Collectors.joining(delimiter));
    }

    public static <T> String join(List<T> originList, String delimiter, Function<T, String> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return null;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return null;
        }
        return originList.stream().map(mapper).filter(CharSequenceUtil::isNotBlank).collect(Collectors.joining(delimiter));
    }

    @SafeVarargs
    public static <T> List<T> filterToList(List<T> originList, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return Arrays.stream(filters).reduce(Predicate::and).map(originList.stream()::filter).orElse(Stream.empty()).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<T> filterDistinctToList(List<T> originList, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Arrays.stream(filters).reduce(Predicate::and).orElse(t -> true)).distinct().collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T, R> List<R> filtersMapToList(List<T> originList, Function<T, R> function, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).map(function).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T, R> List<R> filtersDistinctMapToList(List<T> originList, Function<T, R> function, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).distinct().map(function).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T, R> List<R> filtersMapDistinctToList(List<T> originList, Function<T, R> function, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).map(function).distinct().collect(Collectors.toList());
    }

    @SafeVarargs
    public static <R> List<R> filterBlankDistinctMapToList(List<String> originList, Function<String, R> function, Predicate<String> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeFilter(originList, CharSequenceUtil::isBlank))) {
            return new ArrayList();
        }
        return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).map(function).distinct().collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<String> mapFiltersBlankDistinctToList(List<T> originList, Function<T, String> function, Predicate<String> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList<String>();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList<String>();
        }
        return originList.stream().map(function).filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
    }

    @SafeVarargs
    public static List<String> filterBlankDistinctToList(List<String> originList, Predicate<String> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList<String>();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeFilter(originList, CharSequenceUtil::isBlank))) {
            return new ArrayList<String>();
        }
        return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).distinct().collect(Collectors.toList());
    }

    public static <T> List<T> distinctToList(List<T> list, Function<? super T, ?> keyExtractor) {
        return list.stream().filter(LamUtils.distinctByKey(keyExtractor)).collect(Collectors.toList());
    }

    public static <T> List<T> distinctToList(List<T> originList) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().distinct().collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        ConcurrentHashMap.KeySetView seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <T> List<T> removeNull(List<T> list) {
        return LamUtils.removeFilter(list, Objects::isNull);
    }

    @SafeVarargs
    public static <T> List<T> removeFilter(List<T> originList, Predicate<? super T> ... removeConditions) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        return originList.stream().filter(item -> Stream.of(removeConditions).noneMatch(removeCondition -> removeCondition.test(item))).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> Optional<T> filtersToFindFirstOptional(List<T> originList, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return Optional.empty();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return Optional.empty();
        }
        try {
            return originList.stream().filter(Objects::nonNull).filter(Stream.of(filters).filter(Objects::nonNull).reduce(Predicate::and).orElse(t -> true)).findFirst();
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    @SafeVarargs
    public static <T> T filtersToFindFirst(List<T> originList, Predicate<T> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return null;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return null;
        }
        try {
            return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).findFirst().orElse(null);
        }
        catch (Exception e) {
            return null;
        }
    }

    @SafeVarargs
    public static <T, U> U mapFiltersToFindFirst(List<T> originList, Function<T, U> mapper, Predicate<U> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return null;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return null;
        }
        try {
            return originList.stream().map(mapper).filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).findFirst().orElse(null);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static <T> boolean anyMatch(List<T> originList, Predicate<T> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return false;
        }
        return originList.stream().anyMatch(mapper);
    }

    public static <T> boolean noneMatch(List<T> originList, Predicate<T> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return false;
        }
        return originList.stream().noneMatch(mapper);
    }

    public static <T, R> boolean mapAnyMatch(List<T> originList, Function<T, R> mapper, Predicate<R> predicate) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return false;
        }
        return originList.stream().map(mapper).anyMatch(predicate);
    }

    public static <T, R> boolean mapDistinctAnyMatch(List<T> originList, Function<T, R> mapper, Predicate<R> predicate) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return false;
        }
        return originList.stream().map(mapper).distinct().anyMatch(predicate);
    }

    @SafeVarargs
    public static <T, R> List<R> mapToList(List<T> originList, Function<T, R> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().flatMap(t -> Arrays.stream(filters).filter(Objects::nonNull).map(f -> f.apply(t)).filter(Objects::nonNull)).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T, R> List<R> mapDistinctToList(List<T> originList, Function<T, R> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().flatMap(t -> Arrays.stream(filters).filter(Objects::nonNull).map(f -> f.apply(t)).filter(Objects::nonNull)).distinct().collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T, R> List<R> mapFiltersDistinctToList(List<T> originList, Function<T, R> mapper, Predicate<R> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Objects::nonNull).map(mapper).filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).distinct().collect(Collectors.toList());
    }

    public static <T, R> List<R> mapFiltersToList(List<T> originList, Function<T, R> mapper, Predicate<R> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Objects::nonNull).map(mapper).filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).collect(Collectors.toList());
    }

    public static <T, R> List<R> mapDistinctFiltersToList(List<T> originList, Function<T, R> mapper, Predicate<R> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().filter(Objects::nonNull).map(mapper).distinct().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).collect(Collectors.toList());
    }

    public static <K, V> Map<K, V> toBeanMap(List<V> originList, Function<V, K> keyExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Objects::nonNull).filter(element -> keyExtractor.apply(element) != null).collect(Collectors.toMap(keyExtractor, Function.identity(), (v1, v2) -> v1));
    }

    public static <K, V> Map<K, V> filterToBeanMap(List<V> originList, Predicate<V> filter, Function<V, K> keyExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Objects::nonNull).filter(filter).filter(element -> keyExtractor.apply(element) != null).collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    public static <K, V> Map<K, V> filterToBeanMergeMap(List<V> originList, Predicate<V> filter, Function<V, K> keyExtractor, BinaryOperator<V> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(filter).filter(Objects::nonNull).filter(element -> keyExtractor.apply(element) != null).collect(Collectors.toMap(keyExtractor, Function.identity(), mergeExtractor));
    }

    @SafeVarargs
    public static <K, V> Map<K, V> filtersToBeanMap(List<V> originList, Function<V, K> keyExtractor, Predicate<V> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Objects::nonNull).filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).filter(element -> keyExtractor.apply(element) != null).collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    @SafeVarargs
    public static <T> long count(List<T> originList, Predicate<T> ... filters) {
        return originList.stream().filter(Stream.of(filters).reduce(Predicate::and).orElse(t -> true)).count();
    }

    public static <K, V> Map<K, V> toBeanLinkedMap(List<V> originList, Function<V, K> keyExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Objects::nonNull).filter(element -> keyExtractor.apply(element) != null).collect(Collectors.toMap(keyExtractor, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
    }

    public static <K, V, S> Map<K, S> toMap(List<V> originList, Function<V, K> keyExtractor, Function<V, S> valueExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, (k1, k2) -> k1));
    }

    public static <K, V, S> Map<K, S> toLinkedMap(List<V> originList, Function<V, K> keyExtractor, Function<V, S> valueExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, (k1, k2) -> k1, LinkedHashMap::new));
    }

    public static <K, V, S> Map<K, S> toMergeMap(List<V> originList, Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, mergeExtractor));
    }

    public static <K, V, S> Map<K, S> toMergeLinkedMap(List<V> originList, Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, mergeExtractor, LinkedHashMap::new));
    }

    public static <K, V> Map<K, V> toBeanMergeMap(List<V> originList, Function<V, K> keyExtractor, BinaryOperator<V> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Objects::nonNull).filter(element -> keyExtractor.apply(element) != null).collect(Collectors.toMap(keyExtractor, Function.identity(), mergeExtractor));
    }

    public static <K, V> Map<K, V> toBeanMergeLinkedMap(List<V> originList, Function<V, K> keyExtractor, BinaryOperator<V> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(item -> keyExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, v -> v, mergeExtractor, LinkedHashMap::new));
    }

    public static <K, V, S> Map<K, S> filterToMap(List<V> originList, Predicate<V> filter, Function<V, K> keyExtractor, Function<V, S> valueExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(filter).filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, (k1, k2) -> k1));
    }

    public static <K, V, S> Map<K, S> filterToMergeMap(List<V> originList, Predicate<V> filter, Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(filter).filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, mergeExtractor));
    }

    public static <K, V, S> Map<K, S> filterToMergeLinkedMap(List<V> originList, Predicate<V> filter, Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(filter).filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, mergeExtractor, LinkedHashMap::new));
    }

    @SafeVarargs
    public static <K, V, S> Map<K, S> filtersToMergeMap(List<V> originList, Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor, Predicate<V> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Arrays.stream(filters).reduce(Predicate::and).orElse(t -> true)).filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, mergeExtractor));
    }

    @SafeVarargs
    public static <K, V, S> Map<K, S> filtersToMergeLinkedMap(List<V> originList, Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor, Predicate<V> ... filters) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().filter(Arrays.stream(filters).reduce(Predicate::and).orElse(t -> true)).filter(item -> keyExtractor.apply(item) != null && valueExtractor.apply(item) != null).collect(Collectors.toMap(keyExtractor, valueExtractor, mergeExtractor, LinkedHashMap::new));
    }

    public static <K, V> Map<K, List<V>> groupByToBeanMap(List<V> originList, Function<V, K> keyExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().collect(Collectors.groupingBy(keyExtractor));
    }

    public static <K, V, U> Map<K, List<U>> groupByToMap(List<V> originList, Function<V, K> keyExtractor, Function<V, U> valueExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new HashMap();
        }
        return originList.stream().collect(Collectors.groupingBy(keyExtractor, Collectors.mapping(valueExtractor, Collectors.toList())));
    }

    public static <K, V, U extends Comparable<? super U>> List<V> groupByMaxValueToList(List<V> originList, Function<V, K> keyExtractor, Function<? super V, ? extends U> function) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().collect(Collectors.groupingBy(keyExtractor, Collectors.maxBy(Comparator.comparing(function, Comparator.nullsLast(Comparable::compareTo))))).values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public static <K, V, U extends Comparable<? super U>> List<V> groupByMinValueToList(List<V> originList, Function<V, K> keyExtractor, Function<? super V, ? extends U> function) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().collect(Collectors.groupingBy(keyExtractor, Collectors.minBy(Comparator.comparing(function, Comparator.nullsLast(Comparable::compareTo))))).values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public static <T, U extends Comparable<? super U>> List<T> sortAscLastNullToList(List<T> originList, Function<? super T, ? extends U> function) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet(Comparator.comparing(function, Comparator.nullsLast(Comparable::compareTo)))), ArrayList::new));
    }

    public static <T, U extends Comparable<? super U>> List<T> sortAscFirstNullToList(List<T> originList, Function<? super T, ? extends U> function) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().sorted(Comparator.comparing(function, Comparator.nullsFirst(Comparable::compareTo))).collect(Collectors.toList());
    }

    public static <T, U extends Comparable<? super U>> List<T> sortDescLastNullToList(List<T> originList, Function<? super T, ? extends U> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().sorted(Comparator.comparing(mapper, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
    }

    public static <T, U extends Comparable<? super U>> List<T> sortDescFirstNullToList(List<T> originList, Function<? super T, ? extends U> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return new ArrayList();
        }
        if (CollUtil.isEmpty(originList = LamUtils.removeNull(originList))) {
            return new ArrayList();
        }
        return originList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet(Comparator.comparing(mapper, Comparator.nullsFirst(Comparable::compareTo)).reversed())), ArrayList::new));
    }

    public static <T> List<T>[] getChangeCudAttr(List<T> oldList, List<T> newList, BiFunction<T, T, Boolean> mapper) {
        List existsList = oldList.stream().filter(s -> newList.stream().anyMatch(t -> (Boolean)mapper.apply(t, s))).collect(Collectors.toList());
        List stayAddIds = newList.stream().filter(s -> existsList.stream().noneMatch(t -> (Boolean)mapper.apply(t, s))).collect(Collectors.toList());
        List stayDelIds = oldList.stream().filter(s -> existsList.stream().noneMatch(t -> (Boolean)mapper.apply(t, s))).collect(Collectors.toList());
        return new List[]{stayAddIds, stayDelIds, existsList};
    }

    public static <T> String getFieldName(SFunction<T, ?> sFunction) {
        return XmMap.getField(sFunction);
    }

    public static <T> Method getGetter(Class<T> clazz, SFunction<T, ?> sFunction) {
        return BeanUtil.getBeanDesc(clazz).getGetter(LamUtils.getFieldName(sFunction));
    }

    public static <T> Method getGetter(Class<T> clazz, String fieldName) {
        return BeanUtil.getBeanDesc(clazz).getGetter(fieldName);
    }

    public static <T> LambdaBuilder<T> build(Supplier<T> constructor) {
        return LambdaBuilder.builder(constructor);
    }
}

