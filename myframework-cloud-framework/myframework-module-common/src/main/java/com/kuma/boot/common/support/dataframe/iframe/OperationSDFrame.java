/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.OperationIFrame;
import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import java.util.Collection;
import java.util.Comparator;

public interface OperationSDFrame<T>
extends OperationIFrame<T> {
    @Override
    public SDFrame<T> unionAll(IFrame<T> var1);

    @Override
    public SDFrame<T> unionAll(Collection<T> var1);

    @Override
    public SDFrame<T> union(IFrame<T> var1);

    @Override
    public SDFrame<T> union(IFrame<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> union(Collection<T> var1);

    @Override
    public SDFrame<T> union(Collection<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> retainAll(IFrame<T> var1);

    @Override
    public SDFrame<T> retainAll(IFrame<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> retainAll(Collection<T> var1);

    @Override
    public SDFrame<T> retainAll(Collection<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> intersection(IFrame<T> var1);

    @Override
    public SDFrame<T> intersection(IFrame<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> intersection(Collection<T> var1);

    @Override
    public SDFrame<T> intersection(Collection<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> different(IFrame<T> var1, Comparator<T> var2);

    @Override
    public SDFrame<T> different(Collection<T> var1);

    @Override
    public SDFrame<T> different(Collection<T> var1, Comparator<T> var2);
}

