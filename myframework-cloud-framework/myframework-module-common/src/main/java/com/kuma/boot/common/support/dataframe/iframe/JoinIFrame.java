/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;

public interface JoinIFrame<T> {
    public <R, K> IFrame<R> join(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    public <R, K> IFrame<R> joinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    public <R, K> IFrame<R> join(IFrame<K> var1, JoinOn<T, K> var2);

    public <K> IFrame<T> joinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    public <K> IFrame<T> joinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    public <R, K> IFrame<R> leftJoin(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    public <R, K> IFrame<R> leftJoinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    public <R, K> IFrame<R> leftJoin(IFrame<K> var1, JoinOn<T, K> var2);

    public <K> IFrame<T> leftJoinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    public <K> IFrame<T> leftJoinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    public <R, K> IFrame<R> rightJoin(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    public <R, K> IFrame<R> rightJoinOnce(IFrame<K> var1, JoinOn<T, K> var2, Join<T, K, R> var3);

    public <R, K> IFrame<R> rightJoin(IFrame<K> var1, JoinOn<T, K> var2);

    public <K> IFrame<T> rightJoinVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);

    public <K> IFrame<T> rightJoinOnceVoid(IFrame<K> var1, JoinOn<T, K> var2, VoidJoin<T, K> var3);
}

