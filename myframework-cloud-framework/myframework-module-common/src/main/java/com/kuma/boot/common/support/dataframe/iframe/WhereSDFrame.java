/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WhereIFrame;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface WhereSDFrame<T>
extends WhereIFrame<T> {
    @Override
    public SDFrame<T> where(Predicate<? super T> var1);

    @Override
    public <R> SDFrame<T> whereNull(Function<T, R> var1);

    @Override
    public <R> SDFrame<T> whereNotNull(Function<T, R> var1);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereBetween(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereBetweenN(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereBetweenR(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereBetweenL(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereNotBetween(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereNotBetweenN(Function<T, R> var1, R var2, R var3);

    @Override
    public <R> SDFrame<T> whereIn(Function<T, R> var1, List<R> var2);

    @Override
    public <R> SDFrame<T> whereNotIn(Function<T, R> var1, List<R> var2);

    @Override
    public SDFrame<T> whereTrue(Predicate<T> var1);

    @Override
    public SDFrame<T> whereNotTrue(Predicate<T> var1);

    @Override
    public <R> SDFrame<T> whereEq(Function<T, R> var1, R var2);

    @Override
    public <R> SDFrame<T> whereNotEq(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereGt(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereGe(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereLt(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> SDFrame<T> whereLe(Function<T, R> var1, R var2);

    @Override
    public <R> SDFrame<T> whereLike(Function<T, R> var1, R var2);

    @Override
    public <R> SDFrame<T> whereNotLike(Function<T, R> var1, R var2);

    @Override
    public <R> SDFrame<T> whereLikeLeft(Function<T, R> var1, R var2);

    @Override
    public <R> SDFrame<T> whereLikeRight(Function<T, R> var1, R var2);
}

