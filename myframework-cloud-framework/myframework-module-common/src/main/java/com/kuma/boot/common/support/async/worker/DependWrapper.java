/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.worker;

import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.Objects;

public class DependWrapper {
    private WorkerWrapper<?, ?> dependWrapper;
    private boolean must = true;

    public DependWrapper(WorkerWrapper<?, ?> dependWrapper, boolean must) {
        this.dependWrapper = dependWrapper;
        this.must = must;
    }

    public DependWrapper() {
    }

    public WorkerWrapper<?, ?> getDependWrapper() {
        return this.dependWrapper;
    }

    public void setDependWrapper(WorkerWrapper<?, ?> dependWrapper) {
        this.dependWrapper = dependWrapper;
    }

    public boolean isMust() {
        return this.must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }

    public String toString() {
        return "DependWrapper{dependWrapper=" + String.valueOf(this.dependWrapper) + ", must=" + this.must + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DependWrapper that = (DependWrapper)o;
        return this.must == that.must && Objects.equals(this.dependWrapper, that.dependWrapper);
    }

    public int hashCode() {
        return Objects.hash(this.dependWrapper, this.must);
    }
}

