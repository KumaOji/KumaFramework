/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.iframe.item;

import java.util.Objects;

public class FI4<T1, T2, T3, T4> {
    private T1 c1;
    private T2 c2;
    private T3 c3;
    private T4 c4;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FI4 fi4 = (FI4)o;
        return Objects.equals(this.c1, fi4.c1) && Objects.equals(this.c2, fi4.c2) && Objects.equals(this.c3, fi4.c3) && Objects.equals(this.c4, fi4.c4);
    }

    public int hashCode() {
        return Objects.hash(this.c1, this.c2, this.c3, this.c4);
    }

    public FI4() {
    }

    public FI4(T1 c1, T2 c2, T3 c3, T4 c4) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
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

    public T3 getC3() {
        return this.c3;
    }

    public void setC3(T3 c3) {
        this.c3 = c3;
    }

    public T4 getC4() {
        return this.c4;
    }

    public void setC4(T4 c4) {
        this.c4 = c4;
    }
}

