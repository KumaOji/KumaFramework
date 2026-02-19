/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 *  org.springframework.stereotype.Controller
 */
package com.kuma.boot.office.fastexcel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Controller
@ResponseExcel(fileName="out")
public @interface ExcelController {
    @AliasFor(annotation=Controller.class)
    public String value() default "";
}

