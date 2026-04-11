package com.kuma.boot.flowengine.simpleflow.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAnnotationFlow {
   String[] basePackages() default {};

   Class<?>[] basePackageClasses() default {};

   boolean enableAutoConfiguration() default true;

   int corePoolSize() default 5;

   int maximumPoolSize() default 20;

   int queueCapacity() default 100;
}
