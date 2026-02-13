/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.support;

import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DFList<T> {
    private List<T> data;

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public DFList(List<T> data) {
        this.data = data;
    }

    public DFList<T> first(int n) {
        if (this.data.isEmpty()) {
            return this;
        }
        if (n <= 0) {
            throw new IllegalArgumentException("first N should greater than zero");
        }
        if (n >= this.data.size()) {
            return this;
        }
        this.data = this.data.subList(0, n);
        return this;
    }

    public T first() {
        if (this.data.isEmpty()) {
            return null;
        }
        return this.data.get(0);
    }

    public DFList<T> last(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("last N should greater than zero");
        }
        if (n >= this.data.size()) {
            return this;
        }
        int start = this.data.size() - n + 1;
        this.data = this.data.subList(start, this.data.size());
        return this;
    }

    public DFList<T> sortDesc(Comparator<T> comparator) {
        this.data = this.data.stream().sorted(comparator.reversed()).collect(Collectors.toList());
        return this;
    }

    public DFList<T> sortAsc(Comparator<T> comparator) {
        this.data = this.data.stream().sorted(comparator).collect(Collectors.toList());
        return this;
    }

    public List<T> build() {
        return this.data;
    }

    public <K> Map<K, T> toMap(Function<T, K> function) {
        return this.data.stream().collect(Collectors.toMap(function, e -> e));
    }

    public <K, V> Map<K, V> toMap(Function<T, K> function, Function<T, V> function2) {
        return this.data.stream().collect(Collectors.toMap(function, function2));
    }

    public SDFrame<T> toSDFrame() {
        return SDFrame.read(this.data);
    }

    public JDFrame<T> toJDFrame() {
        return JDFrame.read(this.data);
    }
}

