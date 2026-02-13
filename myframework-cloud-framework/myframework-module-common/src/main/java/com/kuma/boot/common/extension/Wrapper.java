/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Wrapper {
    public String[] matches() default {};

    public String[] mismatches() default {};
}

