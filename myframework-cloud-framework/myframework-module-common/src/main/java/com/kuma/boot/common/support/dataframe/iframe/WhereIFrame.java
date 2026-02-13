/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface WhereIFrame<T> {
    public IFrame<T> where(Predicate<? super T> var1);

    public <R> IFrame<T> whereNull(Function<T, R> var1);

    public <R> IFrame<T> whereNotNull(Function<T, R> var1);

    public <R extends Comparable<R>> IFrame<T> whereBetween(Function<T, R> var1, R var2, R var3);

    public <R extends Comparable<R>> IFrame<T> whereBetweenN(Function<T, R> var1, R var2, R var3);

    public <R extends Comparable<R>> IFrame<T> whereBetweenR(Function<T, R> var1, R var2, R var3);

    public <R extends Comparable<R>> IFrame<T> whereBetweenL(Function<T, R> var1, R var2, R var3);

    public <R extends Comparable<R>> IFrame<T> whereNotBetween(Function<T, R> var1, R var2, R var3);

    public <R extends Comparable<R>> IFrame<T> whereNotBetweenN(Function<T, R> var1, R var2, R var3);

    public <R> IFrame<T> whereIn(Function<T, R> var1, List<R> var2);

    public <R> IFrame<T> whereNotIn(Function<T, R> var1, List<R> var2);

    public IFrame<T> whereTrue(Predicate<T> var1);

    public IFrame<T> whereNotTrue(Predicate<T> var1);

    public <R> IFrame<T> whereEq(Function<T, R> var1, R var2);

    public <R> IFrame<T> whereNotEq(Function<T, R> var1, R var2);

    public <R extends Comparable<R>> IFrame<T> whereGt(Function<T, R> var1, R var2);

    public <R extends Comparable<R>> IFrame<T> whereGe(Function<T, R> var1, R var2);

    public <R extends Comparable<R>> IFrame<T> whereLt(Function<T, R> var1, R var2);

    public <R extends Comparable<R>> IFrame<T> whereLe(Function<T, R> var1, R var2);

    public <R> IFrame<T> whereLike(Function<T, R> var1, R var2);

    public <R> IFrame<T> whereNotLike(Function<T, R> var1, R var2);

    public <R> IFrame<T> whereLikeLeft(Function<T, R> var1, R var2);

    public <R> IFrame<T> whereLikeRight(Function<T, R> var1, R var2);
}

