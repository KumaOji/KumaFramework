/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JacksonAnnotationsInside
 *  tools.jackson.databind.annotation.JsonSerialize
 */
package com.kuma.boot.common.support.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.kuma.boot.common.support.jackson.NumberJsonSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tools.jackson.databind.annotation.JsonSerialize;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using=NumberJsonSerializer.class)
public @interface NumberJsonFormat {
    public String pattern() default "#0.00";

    public boolean simpleTypeSupport() default true;
}

