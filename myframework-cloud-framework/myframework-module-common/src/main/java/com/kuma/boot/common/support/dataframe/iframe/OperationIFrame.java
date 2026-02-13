/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import java.util.Collection;
import java.util.Comparator;

public interface OperationIFrame<T> {
    public IFrame<T> unionAll(IFrame<T> var1);

    public IFrame<T> unionAll(Collection<T> var1);

    public IFrame<T> union(IFrame<T> var1);

    public IFrame<T> union(IFrame<T> var1, Comparator<T> var2);

    public IFrame<T> union(Collection<T> var1);

    public IFrame<T> union(Collection<T> var1, Comparator<T> var2);

    public IFrame<T> retainAll(IFrame<T> var1);

    public IFrame<T> retainAll(IFrame<T> var1, Comparator<T> var2);

    public IFrame<T> retainAll(Collection<T> var1);

    public IFrame<T> retainAll(Collection<T> var1, Comparator<T> var2);

    public IFrame<T> intersection(IFrame<T> var1);

    public IFrame<T> intersection(IFrame<T> var1, Comparator<T> var2);

    public IFrame<T> intersection(Collection<T> var1);

    public IFrame<T> intersection(Collection<T> var1, Comparator<T> var2);

    public IFrame<T> different(IFrame<T> var1);

    public IFrame<T> different(IFrame<T> var1, Comparator<T> var2);

    public IFrame<T> different(Collection<T> var1);

    public IFrame<T> different(Collection<T> var1, Comparator<T> var2);
}

