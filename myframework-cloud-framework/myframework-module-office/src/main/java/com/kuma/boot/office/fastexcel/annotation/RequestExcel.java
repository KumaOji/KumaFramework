/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.fastexcel.annotation;

import com.kuma.boot.office.fastexcel.listener.DefaultExcelMapReadListener;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RequestExcel {
    public Class<? extends ExcelMapReadListener<?>> parse() default DefaultExcelMapReadListener.class;

    public boolean ignoreEmptyRow() default false;
}

