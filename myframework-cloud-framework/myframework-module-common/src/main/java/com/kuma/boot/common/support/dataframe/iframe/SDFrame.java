/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.ConfigurableSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WindowSDFrame;
import com.kuma.boot.common.support.dataframe.iframe.function.ConsumerIndex;
import com.kuma.boot.common.support.dataframe.iframe.function.ListToOneFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.ReplenishFunction;
import com.kuma.boot.common.support.dataframe.iframe.function.SetFunction;
import com.kuma.boot.common.support.dataframe.iframe.impl.SDFrameImpl;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.window.Sorter;
import com.kuma.boot.common.support.dataframe.iframe.window.Window;
import com.kuma.boot.common.support.dataframe.util.FrameUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface SDFrame<T>
extends ConfigurableSDFrame<T> {
    public static <R> SDFrame<R> read(List<R> list) {
        return new SDFrameImpl<R>(list);
    }

    public static <K, V> SDFrame<FI2<K, V>> read(Map<K, V> map) {
        return new SDFrameImpl<FI2<K, V>>(FrameUtil.toListFI2(map));
    }

    public static <K, J, V> SDFrame<FI3<K, J, V>> readMap(Map<K, Map<J, V>> map) {
        return new SDFrameImpl<FI3<K, J, V>>(FrameUtil.toListFI3(map));
    }

    @Override
    public <R> SDFrame<R> from(Stream<R> var1);

    @Override
    public SDFrame<T> forEachDo(Consumer<? super T> var1);

    @Override
    public SDFrame<T> forEachParallel(Consumer<? super T> var1);

    @Override
    public SDFrame<T> forEachDo(ConsumerIndex<? super T> var1);

    @Override
    public SDFrame<T> defaultScale(int var1);

    @Override
    public SDFrame<T> defaultScale(int var1, RoundingMode var2);

    @Override
    public void show();

    @Override
    public void show(int var1);

    @Override
    public List<String> columns();

    @Override
    public <R> List<R> col(Function<T, R> var1);

    @Override
    public <R> SDFrame<R> map(Function<T, R> var1);

    @Override
    public <R> SDFrame<R> mapParallel(Function<T, R> var1);

    @Override
    public <R extends Number> SDFrame<T> mapPercent(Function<T, R> var1, SetFunction<T, BigDecimal> var2, int var3);

    @Override
    public <R extends Number> SDFrame<T> mapPercent(Function<T, R> var1, SetFunction<T, BigDecimal> var2);

    @Override
    public SDFrame<List<T>> partition(int var1);

    @Override
    public SDFrame<FI2<T, Integer>> addRowNumberCol();

    @Override
    public SDFrame<FI2<T, Integer>> addRowNumberCol(Sorter<T> var1);

    @Override
    public SDFrame<T> addRowNumberCol(SetFunction<T, Integer> var1);

    @Override
    public SDFrame<T> addRowNumberCol(Sorter<T> var1, SetFunction<T, Integer> var2);

    @Override
    public SDFrame<FI2<T, Integer>> addRankCol(Sorter<T> var1);

    @Override
    public SDFrame<T> addRankCol(Sorter<T> var1, SetFunction<T, Integer> var2);

    @Override
    public SDFrame<FI2<T, String>> explodeString(Function<T, String> var1, String var2);

    @Override
    public SDFrame<T> explodeString(Function<T, String> var1, SetFunction<T, String> var2, String var3);

    @Override
    public SDFrame<FI2<T, String>> explodeJsonArray(Function<T, String> var1);

    @Override
    public SDFrame<T> explodeJsonArray(Function<T, String> var1, SetFunction<T, String> var2);

    @Override
    public <E> SDFrame<FI2<T, E>> explodeCollection(Function<T, ? extends Collection<E>> var1);

    @Override
    public <E> SDFrame<T> explodeCollection(Function<T, ? extends Collection<E>> var1, SetFunction<T, E> var2);

    @Override
    public <E> SDFrame<FI2<T, E>> explodeCollectionArray(Function<T, ?> var1, Class<E> var2);

    @Override
    public <E> SDFrame<T> explodeCollectionArray(Function<T, ?> var1, SetFunction<T, E> var2, Class<E> var3);

    @Override
    public SDFrame<T> sortDesc(Comparator<T> var1);

    @Override
    public <R extends Comparable<? super R>> SDFrame<T> sortDesc(Function<T, R> var1);

    @Override
    public SDFrame<T> sortAsc(Comparator<T> var1);

    @Override
    public <R extends Comparable<R>> SDFrame<T> sortAsc(Function<T, R> var1);

    @Override
    public SDFrame<T> cutFirst(int var1);

    @Override
    public SDFrame<T> cutLast(int var1);

    @Override
    public SDFrame<T> cut(Integer var1, Integer var2);

    @Override
    public SDFrame<T> cutPage(int var1, int var2);

    @Override
    public SDFrame<T> cutFirstRank(Sorter<T> var1, int var2);

    @Override
    public T head();

    @Override
    public List<T> head(int var1);

    @Override
    public T tail();

    @Override
    public List<T> tail(int var1);

    @Override
    public SDFrame<T> distinct();

    @Override
    public <R extends Comparable<R>> SDFrame<T> distinct(Function<T, R> var1);

    @Override
    public <R extends Comparable<R>> SDFrame<T> distinct(Function<T, R> var1, ListToOneFunction<T> var2);

    @Override
    public SDFrame<T> distinct(Comparator<T> var1);

    @Override
    public SDFrame<T> distinct(Comparator<T> var1, ListToOneFunction<T> var2);

    public WindowSDFrame<T> window(Window<T> var1);

    public WindowSDFrame<T> window();

    @Override
    public <C> SDFrame<T> replenish(Function<T, C> var1, List<C> var2, Function<C, T> var3);

    @Override
    public <G, C> SDFrame<T> replenish(Function<T, G> var1, Function<T, C> var2, List<C> var3, ReplenishFunction<G, C, T> var4);

    @Override
    public <G, C> SDFrame<T> replenish(Function<T, G> var1, Function<T, C> var2, ReplenishFunction<G, C, T> var3);
}

