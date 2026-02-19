/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.test.util;

import java.io.Serializable;

@FunctionalInterface
public interface IGetter<T, R>
extends Serializable {
    public R apply(T var1);
}

