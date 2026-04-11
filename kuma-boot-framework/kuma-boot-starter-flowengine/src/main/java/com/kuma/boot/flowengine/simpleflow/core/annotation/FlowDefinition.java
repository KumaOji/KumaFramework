package com.kuma.boot.flowengine.simpleflow.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlowDefinition {
   String id() default "";

   String name() default "";

   String description() default "";

   String version() default "1.0.0";

   boolean enableParallel() default false;

   int maxParallelism() default 10;

   long timeout() default 30000L;
}
