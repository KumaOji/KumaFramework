package com.kuma.boot.data.jpa.extend;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.annotation.QueryAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@QueryAnnotation
@Documented
public @interface MyQuery {
   String value() default "";

   String countQuery() default "";

   String countProjection() default "";

   boolean nativeQuery() default false;

   boolean expressionQuery() default true;
}
