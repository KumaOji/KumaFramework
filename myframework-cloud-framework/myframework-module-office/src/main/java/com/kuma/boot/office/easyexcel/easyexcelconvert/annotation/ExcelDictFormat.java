/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDictFormat {
    public String dictType() default "";

    public String readConverterExp() default "";

    public String separator() default ",";
}

