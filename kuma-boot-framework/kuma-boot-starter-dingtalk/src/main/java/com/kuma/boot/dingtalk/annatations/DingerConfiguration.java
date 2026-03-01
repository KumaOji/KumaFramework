/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.annatations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
public @interface DingerConfiguration {
    public String tokenId();

    public String decryptKey() default "";

    public String secret() default "";
}

