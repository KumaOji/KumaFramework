/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.WhereIFrame;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface WhereJDFrame<T>
extends WhereIFrame<T> {
    @Override
    public JDFrame<T> where(Predicate<? super T> var1);

    @Override
    public <R> JDFrame<T> whereNull(Function<T, R> var1);

    @Override
    public <R> JDFrame<T> whereNotNull(Function<T, R> var1);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereBetween(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereBetweenN(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereBetweenR(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereBetweenL(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereNotBetween(Function<T, R> var1, R var2, R var3);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereNotBetweenN(Function<T, R> var1, R var2, R var3);

    @Override
    public <R> JDFrame<T> whereIn(Function<T, R> var1, List<R> var2);

    @Override
    public <R> JDFrame<T> whereNotIn(Function<T, R> var1, List<R> var2);

    @Override
    public JDFrame<T> whereTrue(Predicate<T> var1);

    @Override
    public JDFrame<T> whereNotTrue(Predicate<T> var1);

    @Override
    public <R> JDFrame<T> whereEq(Function<T, R> var1, R var2);

    @Override
    public <R> JDFrame<T> whereNotEq(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereGt(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereGe(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereLt(Function<T, R> var1, R var2);

    @Override
    public <R extends Comparable<R>> JDFrame<T> whereLe(Function<T, R> var1, R var2);

    @Override
    public <R> JDFrame<T> whereLike(Function<T, R> var1, R var2);

    @Override
    public <R> JDFrame<T> whereNotLike(Function<T, R> var1, R var2);

    @Override
    public <R> JDFrame<T> whereLikeLeft(Function<T, R> var1, R var2);

    @Override
    public <R> JDFrame<T> whereLikeRight(Function<T, R> var1, R var2);
}

