/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.support;

import java.util.Objects;

public class MaxMin<T> {
    private T max;
    private T min;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MaxMin maxMin = (MaxMin)o;
        return Objects.equals(this.max, maxMin.max) && Objects.equals(this.min, maxMin.min);
    }

    public int hashCode() {
        return Objects.hash(this.max, this.min);
    }

    public MaxMin() {
    }

    public MaxMin(T max, T min) {
        this.max = max;
        this.min = min;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getMin() {
        return this.min;
    }

    public void setMin(T min) {
        this.min = min;
    }
}

