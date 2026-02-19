/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.fastexcel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ResponseExcel {
    public String fileName();

    public String template() default "";

    public Suffix suffix() default Suffix.XLSM;

    public static enum Suffix {
        XLS(".xls"),
        XLSX(".xlsx"),
        XLSM(".xlsm");

        private final String name;

        private Suffix(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

