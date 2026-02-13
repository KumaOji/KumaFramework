/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.info;

import com.kuma.boot.common.support.info.Caller;
import com.kuma.boot.common.support.info.Create;
import com.kuma.boot.common.support.info.Update;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface RpcInfo {
    public Create create();

    public Update[] update() default {};

    public Caller[] caller() default {};
}

