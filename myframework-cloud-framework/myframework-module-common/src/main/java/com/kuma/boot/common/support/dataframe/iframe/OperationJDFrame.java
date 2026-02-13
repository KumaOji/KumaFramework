/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.OperationIFrame;
import java.util.Collection;
import java.util.Comparator;

public interface OperationJDFrame<T>
extends OperationIFrame<T> {
    @Override
    public JDFrame<T> unionAll(IFrame<T> var1);

    @Override
    public JDFrame<T> unionAll(Collection<T> var1);

    @Override
    public JDFrame<T> union(IFrame<T> var1);

    @Override
    public JDFrame<T> union(IFrame<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> union(Collection<T> var1);

    @Override
    public JDFrame<T> union(Collection<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> retainAll(IFrame<T> var1);

    @Override
    public JDFrame<T> retainAll(IFrame<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> retainAll(Collection<T> var1);

    @Override
    public JDFrame<T> retainAll(Collection<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> intersection(IFrame<T> var1);

    @Override
    public JDFrame<T> intersection(IFrame<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> intersection(Collection<T> var1);

    @Override
    public JDFrame<T> intersection(Collection<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> different(IFrame<T> var1, Comparator<T> var2);

    @Override
    public JDFrame<T> different(Collection<T> var1);

    @Override
    public JDFrame<T> different(Collection<T> var1, Comparator<T> var2);
}

