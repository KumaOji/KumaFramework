package com.kuma.boot.translation.translationjackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.kuma.boot.translation.core.handler.TranslationHandler;
import com.kuma.boot.translation.translationjackson.core.DefaultDictTranslation;
import com.kuma.boot.translation.translationjackson.core.Translation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tools.jackson.databind.annotation.JsonSerialize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@JacksonAnnotationsInside
@JsonSerialize(
   using = TranslationHandler.class
)
public @interface Trans {
   Class<? extends Translation> translationClass() default DefaultDictTranslation.class;

   String alias() default "";

   String readConverterExp() default "";

   boolean isFlat() default false;
}
