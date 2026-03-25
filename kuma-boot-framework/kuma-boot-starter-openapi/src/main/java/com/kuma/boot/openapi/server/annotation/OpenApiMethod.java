package com.kuma.boot.openapi.server.annotation;

import com.kuma.boot.openapi.common.enums.CryModeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OpenApiMethod {
   String value() default "";

   String retEncrypt() default "";

   CryModeEnum cryModeEnum() default CryModeEnum.UNKNOWN;

   String enableCompress() default "";
}
