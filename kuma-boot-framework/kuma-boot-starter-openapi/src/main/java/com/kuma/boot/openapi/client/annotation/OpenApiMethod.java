package com.kuma.boot.openapi.client.annotation;

import com.kuma.boot.openapi.common.enums.CryModeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OpenApiMethod {
   String value() default "";

   String retDecrypt() default "";

   CryModeEnum cryModeEnum() default CryModeEnum.UNKNOWN;

   int httpConnectionTimeout() default -1;

   int httpReadTimeout() default -1;

   String enableCompress() default "";
}
