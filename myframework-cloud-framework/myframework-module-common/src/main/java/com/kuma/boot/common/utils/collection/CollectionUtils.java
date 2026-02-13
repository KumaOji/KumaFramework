/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package com.kuma.boot.common.utils.collection;

import com.google.common.collect.Lists;
import com.kuma.boot.common.support.condition.Condition;
import com.kuma.boot.common.support.filler.Filler;
import com.kuma.boot.common.support.filter.Filter;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

public class CollectionUtils
extends org.springframework.util.CollectionUtils {
    public static final List EMPTY_LIST = Collections.emptyList();

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !CollectionUtils.isEmpty(map);
    }

    public static <T> boolean contains(@Nullable T[] array, T element) {
        if (array == null) {
            return false;
        }
        return Arrays.stream(array).anyMatch(x -> ObjectUtils.nullSafeEquals((Object)x, (Object)element));
    }

    public static String[] concat(String[] one, String[] other) {
        return CollectionUtils.concat(one, other, String.class);
    }

    public static <T> T[] concat(T[] one, T[] other, Class<T> clazz) {
        Object[] target = (Object[])Array.newInstance(clazz, one.length + other.length);
        System.arraycopy(one, 0, target, 0, one.length);
        System.arraycopy(other, 0, target, one.length, other.length);
        return target;
    }

    @SafeVarargs
    public static <E> Set<E> ofImmutableSet(E ... es) {
        return Arrays.stream(Objects.requireNonNull(es, "args es is null.")).collect(Collectors.toSet());
    }

    @SafeVarargs
    public static <E> List<E> ofImmutableList(E ... es) {
        return Arrays.stream(Objects.requireNonNull(es, "args es is null.")).toList();
    }

    public static <E> List<E> toList(Iterable<E> elements) {
        Objects.requireNonNull(elements, "elements es is null.");
        if (elements instanceof Collection) {
            return new ArrayList((Collection)elements);
        }
        Iterator<E> iterator = elements.iterator();
        ArrayList<E> list = new ArrayList<E>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public static <K, V> Map<K, V> toMap(Object ... keysValues) {
        int kvLength = keysValues.length;
        if (kvLength % 2 != 0) {
            throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
        }
        HashMap<Object, Object> keyValueMap = new HashMap<Object, Object>(kvLength);
        for (int i = kvLength - 2; i >= 0; i -= 2) {
            Object key = keysValues[i];
            Object value = keysValues[i + 1];
            keyValueMap.put(key, value);
        }
        return keyValueMap;
    }

    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        Objects.requireNonNull(list, "List to partition must not null.");
        Assert.isTrue((size > 0 ? 1 : 0) != 0, (String)"List to partition size must more then zero.");
        return list instanceof RandomAccess ? new RandomAccessPartition<T>(list, size) : new Partition<T>(list, size);
    }

    public static boolean isEmpty(Collection collection) {
        return null == collection || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    @SafeVarargs
    public static <T> List<T> toList(T ... t) {
        if (t != null) {
            return Arrays.asList(t);
        }
        return Collections.emptyList();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !org.springframework.util.CollectionUtils.isEmpty((Collection)collection);
    }

    public static List<String> arrayToList(String[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList((Object[])array);
    }

    public static String[] listToArray(List<String> stringList) {
        String[] strings = new String[stringList.size()];
        return stringList.toArray(strings);
    }

    public static List<String> trimCollection(Collection<String> stringList) {
        if (CollectionUtils.isEmpty(stringList)) {
            return Collections.emptyList();
        }
        ArrayList resultList = Lists.newArrayList();
        for (String original : stringList) {
            resultList.add(original.trim());
        }
        return resultList;
    }

    public static <T, R> Collection<R> buildCollection(Collection<T> targets, Handler<T, R> handler) {
        if (org.springframework.util.CollectionUtils.isEmpty(targets)) {
            return Collections.emptyList();
        }
        ArrayList<R> rList = new ArrayList<R>();
        for (T t : targets) {
            R r = handler.handle(t);
            if (!ObjectUtils.isNotNull(r)) continue;
            rList.add(r);
        }
        return rList;
    }

    public static <T, R> List<R> buildCollection(T[] targets, Handler<T, R> handler) {
        if (ArrayUtils.isEmpty(targets)) {
            return Collections.emptyList();
        }
        ArrayList<R> rList = new ArrayList<R>(targets.length);
        for (T t : targets) {
            R r = handler.handle(t);
            if (!ObjectUtils.isNotNull(r)) continue;
            rList.add(r);
        }
        return rList;
    }

    public static <T> void addArray(Collection<T> collection, T[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return;
        }
        collection.addAll(Lists.newArrayList((Object[])array));
    }

    public static <K, V> List<K> toList(Iterable<V> values, Handler<? super V, K> keyFunction) {
        if (ObjectUtils.isNull(values)) {
            return Collections.emptyList();
        }
        return CollectionUtils.toList(values.iterator(), keyFunction);
    }

    public static <K, V> List<K> toList(Iterator<V> values, Handler<? super V, K> keyFunction) {
        if (ObjectUtils.isNull(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>();
        while (values.hasNext()) {
            V value = values.next();
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static <E> List<E> fillList(List<E> values, Filler<E> filler) {
        if (ObjectUtils.isNull(values)) {
            return values;
        }
        for (E e : values) {
            filler.fill(e);
        }
        return values;
    }

    public static List<String> splitByAnyBlank(String string) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        String pattern = "\\s+";
        Object[] strings = string.split(pattern);
        return Lists.newArrayList((Object[])strings);
    }

    public static <T> List<T> filterList(List<T> list, Filter<T> filter) {
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        ArrayList<T> resultList = new ArrayList<T>();
        for (T t : list) {
            if (filter.filter(t)) continue;
            resultList.add(t);
        }
        return resultList;
    }

    public static <T> List<T> conditionList(List<T> list, Condition<T> condition) {
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        ArrayList<T> resultList = new ArrayList<T>();
        for (T t : list) {
            if (!condition.condition(t)) continue;
            resultList.add(t);
        }
        return resultList;
    }

    public static List<String> toStringList(List<?> pathList) {
        if (CollectionUtils.isEmpty(pathList)) {
            return Collections.emptyList();
        }
        ArrayList<String> stringList = new ArrayList<String>(pathList.size());
        for (Object object : pathList) {
            if (!ObjectUtils.isNotNull(object)) continue;
            stringList.add(object.toString());
        }
        return stringList;
    }

    public static <T> Optional<T> firstNotNullElem(Collection<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }
        for (T elem : list) {
            if (!ObjectUtils.isNotNull(elem)) continue;
            return Optional.of(elem);
        }
        return Optional.empty();
    }

    public static String join(Collection<String> stringCollection, String connector) {
        return StringUtils.join(stringCollection, connector);
    }

    public static String join(Collection<String> stringCollection) {
        return StringUtils.join(stringCollection, ",");
    }

    public static <E> void foreach(Collection<E> collection, Handler<E, Void> handler) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        for (E e : collection) {
            handler.handle(e);
        }
    }

    public static <E> void foreachPrint(Collection<E> collection) {
        CollectionUtils.foreach(collection, new Handler<E, Void>(){

            @Override
            public Void handle(E e) {
                LogUtils.info("e: {}", e);
                return null;
            }
        });
    }

    public static <E> List<E> fill(int size, E elem) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < size; ++i) {
            list.add(elem);
        }
        return list;
    }

    public static List<Integer> fill(int size, int initValue) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < size; ++i) {
            list.add(i + initValue);
        }
        return list;
    }

    public static List<Integer> fill(int size) {
        return CollectionUtils.fill(size, 0);
    }

    public static <E> E getFirst(List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> List<T> distinct(Collection<T> collection) {
        if (org.springframework.util.CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }
        if (collection instanceof Set) {
            return new ArrayList<T>(collection);
        }
        return new ArrayList<T>(new LinkedHashSet<T>(collection));
    }

    public static <T extends Comparable> List<T> distinctAndSort(Collection<T> collection) {
        List<T> list = CollectionUtils.distinct(collection);
        return CollectionUtils.sort(list);
    }

    public static <T extends Comparable> List<T> getRepeatList(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }
        ArrayList resultList = Lists.newArrayList();
        HashSet<Comparable> set = new HashSet<Comparable>();
        for (Comparable elem : collection) {
            if (set.contains(elem)) {
                resultList.add(elem);
            }
            set.add(elem);
        }
        return resultList;
    }

    public static <T extends Comparable> List<T> sort(List<T> collection) {
        if (org.springframework.util.CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }
        Collections.sort(collection);
        return new ArrayList<T>(collection);
    }

    public static int getStartIndex(int startIndex, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return 0;
        }
        if (startIndex < 0 || startIndex > collection.size() - 1) {
            return 0;
        }
        return startIndex;
    }

    public static int getEndIndex(int endIndex, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return 0;
        }
        int maxIndex = collection.size() - 1;
        if (endIndex < 0 || endIndex > maxIndex) {
            return maxIndex;
        }
        return endIndex;
    }

    public static <E> List<E> union(Collection<E> collectionOne, Collection<E> collectionTwo) {
        LinkedHashSet<E> set = new LinkedHashSet<E>();
        set.addAll(collectionOne);
        set.addAll(collectionTwo);
        return new ArrayList(set);
    }

    public static <E> List<E> difference(Collection<E> collectionOne, Collection<E> collectionTwo) {
        LinkedHashSet<E> set = new LinkedHashSet<E>();
        set.addAll(collectionOne);
        set.removeAll(collectionTwo);
        return new ArrayList(set);
    }

    public static <E> List<E> interSection(Collection<E> collectionOne, Collection<E> collectionTwo) {
        LinkedHashSet<E> set = new LinkedHashSet<E>();
        for (E e : collectionOne) {
            if (!collectionTwo.contains(e)) continue;
            set.add(e);
        }
        return new ArrayList(set);
    }

    public static boolean containAny(Collection<String> firstList, Collection<String> secondList) {
        if (CollectionUtils.isEmpty(firstList) || CollectionUtils.isEmpty(secondList)) {
            return false;
        }
        for (String second : secondList) {
            if (!firstList.contains(second)) continue;
            return true;
        }
        return false;
    }

    public static String getLast(List<String> resultList) {
        if (CollectionUtils.isEmpty(resultList)) {
            return "";
        }
        return resultList.get(resultList.size() - 1);
    }

    public static void setLast(List<String> resultList, String line) {
        if (resultList == null) {
            resultList = new ArrayList<String>();
        }
        if (CollectionUtils.isEmpty(resultList)) {
            resultList.add(line);
        }
        resultList.set(resultList.size() - 1, line);
    }

    public static <T> List<T> getTopK(Collection<T> collection, int size) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }
        int actualSize = Math.min(collection.size(), size);
        ArrayList resultList = Lists.newArrayList();
        for (T t : collection) {
            resultList.add(t);
            if (resultList.size() < actualSize) continue;
            break;
        }
        return resultList;
    }

    public static List<String> replaceAll(Collection<String> collection, String regex, String target) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyList();
        }
        ArrayList resultList = Lists.newArrayList();
        for (String s : collection) {
            String result = s.replaceAll(regex, target);
            resultList.add(result);
        }
        return resultList;
    }

    public static <E> List<E> subList(List<E> list, int offset, int limit) {
        ArgUtils.notNegative(offset, "offset");
        ArgUtils.notNegative(limit, "limit");
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        int size = list.size();
        int actualOffset = Math.min(offset, size);
        int actualLimit = Math.min(limit, size - actualOffset);
        ArrayList resultList = Lists.newArrayList();
        for (int i = actualOffset; i < actualOffset + actualLimit; ++i) {
            resultList.add(list.get(i));
        }
        return resultList;
    }

    public static <E> E random(List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int next = ((Random)random).nextInt(list.size());
        return list.get(next);
    }

    public static <T> List<T> list() {
        return Collections.emptyList();
    }

    public static <T> List<T> list(T t) {
        return Collections.singletonList(t);
    }

    @SafeVarargs
    public static <T> List<T> list(T ... ts) {
        return new ArrayList<T>(Arrays.asList(ts));
    }

    public static <T> List<T> copy(List<T> list) {
        return new ArrayList<T>(list);
    }

    public static <T> T head(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T tail(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static <T> List<T> append(List<T> list, T t) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.add(t);
        return list;
    }

    public static <T> List<T> reverse(List<T> list, T t) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        ArrayList<T> results = new ArrayList<T>(list.size());
        for (int i = list.size() - 1; i >= 0; --i) {
            results.add(list.get(i));
        }
        list.add(t);
        return results;
    }

    public static <T> void addAll(Collection<T> collection, Collection<T> addCollection) {
        if (CollectionUtils.isNotEmpty(addCollection)) {
            collection.addAll(addCollection);
        }
    }

    private static class RandomAccessPartition<T>
    extends Partition<T>
    implements RandomAccess {
        RandomAccessPartition(List<T> list, int size) {
            super(list, size);
        }
    }

    private static class Partition<T>
    extends AbstractList<List<T>> {
        final List<T> list;
        final int size;

        Partition(List<T> list, int size) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get(int index) {
            if (index >= 0 && index < this.size()) {
                int start = index * this.size;
                int end = Math.min(start + this.size, this.list.size());
                return this.list.subList(start, end);
            }
            throw new IndexOutOfBoundsException(String.format("index (%s) must be less than size (%s)", index, this.size()));
        }

        @Override
        public int size() {
            return Partition.ceilDiv(this.list.size(), this.size);
        }

        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        private static int ceilDiv(int x, int y) {
            int r = x / y;
            if (r * y < x) {
                ++r;
            }
            return r;
        }
    }
}

