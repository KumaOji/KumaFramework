package com.kuma.boot.sms.common.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnProperty(
   prefix = "kuma.boot.sms",
   name = {"enabled"},
   havingValue = "true"
)
public @interface ConditionalOnSmsEnabled {
}
