package com.kuma.boot.sensitive.sensitivelog.annotation.metadata;

import com.kuma.boot.sensitive.sensitivelog.api.Condition;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveCondition {
   Class<? extends Condition> value();
}
