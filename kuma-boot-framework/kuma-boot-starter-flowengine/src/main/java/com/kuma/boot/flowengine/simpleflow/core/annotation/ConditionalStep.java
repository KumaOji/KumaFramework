package com.kuma.boot.flowengine.simpleflow.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalStep {
   String id() default "";

   String name() default "";

   String description() default "";

   int order() default 0;

   String[] dependsOn() default {};

   String[] onTrue() default {};

   String[] onFalse() default {};

   long timeout() default 5000L;

   int retryCount() default 0;

   long retryInterval() default 1000L;
}
