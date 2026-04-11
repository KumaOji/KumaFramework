package com.kuma.boot.sensitive.sensitivemvc.annocation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.kuma.boot.sensitive.sensitivemvc.SensitiveStrategy;
import com.kuma.boot.sensitive.sensitivemvc.serializer.JacksonSensitiveSerializer;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tools.jackson.databind.annotation.JsonSerialize;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@JacksonAnnotationsInside
@JsonSerialize(
   using = JacksonSensitiveSerializer.class
)
public @interface Sensitive {
   SensitiveStrategy strategy();

   char replacer() default '*';
}
