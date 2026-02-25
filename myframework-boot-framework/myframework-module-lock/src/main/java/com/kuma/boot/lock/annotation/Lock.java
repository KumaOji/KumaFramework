/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.lock.annotation;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Lock {
    public String key();

    public long waitTime() default 0L;

    public long leaseTime() default -1L;

    public TimeUnit unit() default TimeUnit.SECONDS;

    public boolean async() default false;

    public LockTypeEnums type() default LockTypeEnums.LOCK;

    public LockScopeEnum scope();

    public boolean transactional() default false;
}

