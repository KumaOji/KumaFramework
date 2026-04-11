package com.kuma.boot.sensitive.sensitivemvc.annocation;

import com.kuma.boot.sensitive.sensitivemvc.CustomizeSensitiveHandler;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface SensitiveHandler {
   Class<? extends CustomizeSensitiveHandler> value();
}
