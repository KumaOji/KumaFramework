/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.JoinIFrame;
import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;

public interface JoinSDFrame<T>
extends JoinIFrame<T> {
    @Override
    public <R, K> SDFrame<R> join(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> SDFrame<R> joinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> SDFrame<R> join(IFrame<K> var1, JoinOn<T, K> var2);

    @Override
    public <K> SDFrame<T> joinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <K> SDFrame<T> joinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <R, K> SDFrame<R> leftJoin(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> SDFrame<R> leftJoinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> SDFrame<R> leftJoin(IFrame<K> var1, JoinOn<T, K> var2);

    @Override
    public <K> SDFrame<T> leftJoinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <K> SDFrame<T> leftJoinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <R, K> SDFrame<R> rightJoin(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> SDFrame<R> rightJoinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    @Override
    public <R, K> SDFrame<R> rightJoin(IFrame<K> var1, JoinOn<T, K> var2);

    @Override
    public <K> SDFrame<T> rightJoinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    @Override
    public <K> SDFrame<T> rightJoinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);
}

