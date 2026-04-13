package com.kuma.boot.data.jpa.fenix.jpa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.annotation.QueryAnnotation;

@Documented
@QueryAnnotation
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryFenix {
   String value() default "";

   String countQuery() default "";

   boolean nativeQuery() default false;

   Class<?> provider() default Void.class;

   String method() default "";

   String countMethod() default "";

   boolean enableDistinct() default false;

   Class<?> resultType() default Void.class;

   Class<? extends AbstractResultTransformer> resultTransformer() default FenixResultTransformer.class;
}
