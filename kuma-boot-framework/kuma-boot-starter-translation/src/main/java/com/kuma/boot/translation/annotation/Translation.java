package com.kuma.boot.translation.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.kuma.boot.translation.core.handler.TranslationHandler;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tools.jackson.databind.annotation.JsonSerialize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
@JacksonAnnotationsInside
@JsonSerialize(
   using = TranslationHandler.class
)
public @interface Translation {
   String type();

   String mapper() default "";

   String other() default "";
}
