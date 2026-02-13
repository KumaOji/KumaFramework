/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.pair;

import java.util.Objects;

public class Pair<L, R> {
    private static final Pair<Object, Object> EMPTY = new Pair<Object, Object>(null, null);
    private final L left;
    private final R right;

    public static <L, R> Pair<L, R> empty() {
        return EMPTY;
    }

    public static <L, R> Pair<L, R> createLeft(L left) {
        if (left == null) {
            return Pair.empty();
        }
        return new Pair<L, Object>(left, null);
    }

    public static <L, R> Pair<L, R> createRight(R right) {
        if (right == null) {
            return Pair.empty();
        }
        return new Pair<Object, R>(null, right);
    }

    public static <L, R> Pair<L, R> create(L left, R right) {
        if (right == null && left == null) {
            return Pair.empty();
        }
        return new Pair<L, R>(left, right);
    }

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair)o;
        return Objects.equals(this.left, pair.left) && Objects.equals(this.right, pair.right);
    }

    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }

    public String toString() {
        return "Pair{left=" + String.valueOf(this.left) + ", right=" + String.valueOf(this.right) + "}";
    }
}

