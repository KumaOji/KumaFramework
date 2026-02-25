/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotetduplicate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PreventDuplicateSubmit {
    public DuplicateTypeEnum type() default DuplicateTypeEnum.IP;

    public boolean global() default false;

    public int expire() default 1;
}

