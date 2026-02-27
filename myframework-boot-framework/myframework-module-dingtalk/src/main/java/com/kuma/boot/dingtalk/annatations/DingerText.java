/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.annatations;

import com.kuma.boot.dingtalk.enums.AsyncExecuteType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@Documented
public @interface DingerText {
    public String value();

    public boolean atAll() default false;

    public String[] phones() default {};

    public DingerTokenId tokenId() default @DingerTokenId(value="");

    public AsyncExecuteType asyncExecute() default AsyncExecuteType.NONE;
}

