/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.dingtalk.annatations;

import com.kuma.boot.dingtalk.spring.DingerScannerRegistrar;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@Import(value={DingerScannerRegistrar.class})
public @interface DingerScan {
    public String[] value() default {};

    public String[] basePackages() default {};

    public Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    public Class<? extends Annotation> annotationClass() default Annotation.class;

    public Class<?> markerInterface() default Class.class;
}

