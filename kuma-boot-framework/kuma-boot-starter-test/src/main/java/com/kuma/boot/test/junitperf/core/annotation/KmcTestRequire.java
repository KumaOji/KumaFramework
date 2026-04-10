package com.kuma.boot.test.junitperf.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Documented
@API(
   status = Status.MAINTAINED,
   since = "2_0_0"
)
public @interface KmcTestRequire {
   float min() default -1.0F;

   float max() default -1.0F;

   float average() default -1.0F;

   String[] percentiles() default {};

   int timesPerSecond() default 0;
}
