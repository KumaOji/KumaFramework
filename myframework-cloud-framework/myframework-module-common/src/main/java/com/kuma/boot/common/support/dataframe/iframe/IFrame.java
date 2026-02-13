/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.GroupIFrame;
import com.kuma.boot.common.support.dataframe.iframe.JoinIFrame;
import com.kuma.boot.common.support.dataframe.iframe.OperationIFrame;
import com.kuma.boot.common.support.dataframe.iframe.SummaryFrame;
import com.kuma.boot.common.support.dataframe.iframe.WhereIFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.ConsumerIndex;
import com.kuma.boot.common.support.dataframe.iframe.function.ListToOneFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.ReplenishFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface IFrame<T>
extends SummaryFrame<T>,
WhereIFrame<T>,
JoinIFrame<T>,
GroupIFrame<T>,
OperationIFrame<T>,
Iterable<T> {
    public List<T> toLists();

    public T[] toArray();

    public T[] toArray(Class<T> var1);

    public <K, V> Map<K, V> toMap(Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2);

    public <K, K2, V> Map<K, Map<K2, V>> toMap(Function<? super T, ? extends K> var1, Function<? super T, ? extends K2> var2, Function<? super T, ? extends V> var3);

    public Stream<T> stream();

    public <R> IFrame<R> from(Stream<R> var1);

    public IFrame<T> forEachDo(Consumer<? super T> var1);

    public IFrame<T> forEachParallel(Consumer<? super T> var1);

    public IFrame<T> forEachDo(ConsumerIndex<? super T> var1);

    public boolean contains(T var1);

    public <U> boolean containsValue(Function<T, U> var1, U var2);

    public <U> String joining(Function<T, U> var1, CharSequence var2, CharSequence var3, CharSequence var4);

    public <U> String joining(Function<T, U> var1, CharSequence var2);

    public IFrame<T> defaultScale(int var1);

    public IFrame<T> defaultScale(int var1, RoundingMode var2);

    public void show();

    public void show(int var1);

    public List<String> columns();

    public <R> List<R> col(Function<T, R> var1);

    public List<T> page(int var1, int var2);

    public boolean isEmpty();

    public boolean isNotEmpty();

    public <R> IFrame<R> map(Function<T, R> var1);

    public <R> IFrame<R> mapParallel(Function<T, R> var1);

    public <R extends Number> IFrame<T> mapPercent(Function<T, R> var1, SetFunction<T, BigDecimal> var2, int var3);

    public <R extends Number> IFrame<T> mapPercent(Function<T, R> var1, SetFunction<T, BigDecimal> var2);

    public IFrame<List<T>> partition(int var1);

    public IFrame<FI2<T, Integer>> addRowNumberCol();

    public IFrame<FI2<T, Integer>> addRowNumberCol(Sorter<T> var1);

    public IFrame<T> addRowNumberCol(SetFunction<T, Integer> var1);

    public IFrame<T> addRowNumberCol(Sorter<T> var1, SetFunction<T, Integer> var2);

    public IFrame<FI2<T, Integer>> addRankCol(Sorter<T> var1);

    public IFrame<T> addRankCol(Sorter<T> var1, SetFunction<T, Integer> var2);

    public IFrame<FI2<T, String>> explodeString(Function<T, String> var1, String var2);

    public IFrame<T> explodeString(Function<T, String> var1, SetFunction<T, String> var2, String var3);

    public IFrame<FI2<T, String>> explodeJsonArray(Function<T, String> var1);

    public IFrame<T> explodeJsonArray(Function<T, String> var1, SetFunction<T, String> var2);

    public <E> IFrame<FI2<T, E>> explodeCollection(Function<T, ? extends Collection<E>> var1);

    public <E> IFrame<T> explodeCollection(Function<T, ? extends Collection<E>> var1, SetFunction<T, E> var2);

    public <E> IFrame<FI2<T, E>> explodeCollectionArray(Function<T, ?> var1, Class<E> var2);

    public <E> IFrame<T> explodeCollectionArray(Function<T, ?> var1, SetFunction<T, E> var2, Class<E> var3);

    public IFrame<T> sortDesc(Comparator<T> var1);

    public <R extends Comparable<? super R>> IFrame<T> sortDesc(Function<T, R> var1);

    public IFrame<T> sortAsc(Comparator<T> var1);

    public <R extends Comparable<R>> IFrame<T> sortAsc(Function<T, R> var1);

    public IFrame<T> cutFirst(int var1);

    public IFrame<T> cutLast(int var1);

    public IFrame<T> cut(Integer var1, Integer var2);

    public IFrame<T> cutPage(int var1, int var2);

    public IFrame<T> cutFirstRank(Sorter<T> var1, int var2);

    public T head();

    public List<T> head(int var1);

    public T tail();

    public List<T> tail(int var1);

    public List<T> getList(Integer var1, Integer var2);

    public IFrame<T> distinct();

    public <R extends Comparable<R>> IFrame<T> distinct(Function<T, R> var1);

    public <R extends Comparable<R>> IFrame<T> distinct(Function<T, R> var1, ListToOneFunction<T> var2);

    public IFrame<T> distinct(Comparator<T> var1);

    public IFrame<T> distinct(Comparator<T> var1, ListToOneFunction<T> var2);

    public <C> IFrame<T> replenish(Function<T, C> var1, List<C> var2, Function<C, T> var3);

    public <G, C> IFrame<T> replenish(Function<T, G> var1, Function<T, C> var2, List<C> var3, ReplenishFunction<G, C, T> var4);

    public <G, C> IFrame<T> replenish(Function<T, G> var1, Function<T, C> var2, ReplenishFunction<G, C, T> var3);
}

