package com.kuma.boot.sensitive.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.kuma.boot.sensitive.desensitize.enums.SensitiveStrategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tools.jackson.databind.annotation.JsonSerialize;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(
   using = SensitiveJsonSerializer.class
)
public @interface Sensitive {
   SensitiveStrategy strategy();
}
