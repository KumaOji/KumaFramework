package com.kuma.boot.sensitive.sensitivejackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import tools.jackson.databind.annotation.JsonSerialize;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(
   using = SensitiveInfoSerialize.class
)
public @interface SensitiveInfo {
   SensitiveType value();
}
