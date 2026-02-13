/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.item;

import java.util.Objects;

public class FI2<T1, T2> {
    private T1 c1;
    private T2 c2;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FI2 fi2 = (FI2)o;
        return Objects.equals(this.c1, fi2.c1) && Objects.equals(this.c2, fi2.c2);
    }

    public int hashCode() {
        return Objects.hash(this.c1, this.c2);
    }

    public FI2() {
    }

    public FI2(T1 c1, T2 c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public T1 getC1() {
        return this.c1;
    }

    public void setC1(T1 c1) {
        this.c1 = c1;
    }

    public T2 getC2() {
        return this.c2;
    }

    public void setC2(T2 c2) {
        this.c2 = c2;
    }
}

