/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.JoinIFrame;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;

public interface JoinJDFrame<T>
extends JoinIFrame<T> {
    @Override
    public <R, K> JDFrame<R> join(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> JDFrame<R> joinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> JDFrame<R> join(IFrame<K> var1, JoinOn<T, K> var2);

    @Override
    public <K> JDFrame<T> joinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <K> JDFrame<T> joinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <R, K> JDFrame<R> leftJoin(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> JDFrame<R> leftJoinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> JDFrame<R> leftJoin(IFrame<K> var1, JoinOn<T, K> var2);

    @Override
    public <K> JDFrame<T> leftJoinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <K> JDFrame<T> leftJoinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <R, K> JDFrame<R> rightJoin(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> JDFrame<R> rightJoinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> JDFrame<R> rightJoin(IFrame<K> var1, JoinOn<T, K> var2);

    @Override
    public <K> JDFrame<T> rightJoinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <K> JDFrame<T> rightJoinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);
}

