package com.kuma.boot.retry.annotation;

import com.kuma.boot.retry.exception.GuavaRetryException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GuavaRetrying {
   Class[] exceptionClass() default {GuavaRetryException.class};

   int attemptNumber() default 0;

   long waitStrategySleepTime() default 0L;

   long duration() default 0L;
}
