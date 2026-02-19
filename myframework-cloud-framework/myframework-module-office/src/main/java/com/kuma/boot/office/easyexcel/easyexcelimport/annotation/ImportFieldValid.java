/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.easyexcel.easyexcelimport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ImportFieldValid {
    public String message() default "\u8be5\u5b57\u6bb5\u4e0d\u80fd\u4e3a\u7a7a";
}

