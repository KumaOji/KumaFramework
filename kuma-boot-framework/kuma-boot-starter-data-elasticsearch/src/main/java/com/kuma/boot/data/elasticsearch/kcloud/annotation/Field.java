package com.kuma.boot.data.elasticsearch.kcloud.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Field {
   String value() default "";

   Type type();

   String searchAnalyzer() default "";

   String analyzer() default "";

   boolean fielddata() default false;

   boolean eagerGlobalOrdinals() default false;
}
