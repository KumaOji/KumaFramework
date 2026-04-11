package com.kuma.boot.flowengine.simpleflow.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlowStep {
   String id() default "";

   String name() default "";

   String description() default "";

   StepType type() default FlowStep.StepType.SERVICE;

   int order() default 0;

   String[] dependsOn() default {};

   String condition() default "";

   boolean async() default false;

   long timeout() default 10000L;

   int retryCount() default 0;

   long retryInterval() default 1000L;

   public static enum StepType {
      SERVICE,
      CONDITIONAL,
      SIMPLE;

      private StepType() {
      }

      // $FF: synthetic method
      private static StepType[] $values() {
         return new StepType[]{SERVICE, CONDITIONAL, SIMPLE};
      }
   }
}
