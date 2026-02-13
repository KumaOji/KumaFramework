/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.unit;

import com.kuma.boot.common.utils.unit.UnitConvertType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface BigDecimalConvert {
    public UnitConvertType name();
}

