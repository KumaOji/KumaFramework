package com.kuma.boot.sensitive.sensitivelog.annotation.strategy;

import com.kuma.boot.sensitive.sensitivelog.annotation.metadata.SensitiveStrategy;
import com.kuma.boot.sensitive.sensitivelog.api.impl.SensitiveStrategyBuiltIn;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@SensitiveStrategy(SensitiveStrategyBuiltIn.class)
public @interface SensitiveStrategyPhone {
}
