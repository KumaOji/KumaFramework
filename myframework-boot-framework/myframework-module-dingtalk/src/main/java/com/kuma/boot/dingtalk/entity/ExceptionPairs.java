/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

public interface ExceptionPairs
extends Pairs<Integer, String> {
    @Override
    default public String message(Object ... args) {
        String message = String.format((String)this.desc(), args);
        return String.format("{code=%d, message=%s}", this.code(), message);
    }
}

