package com.kuma.boot.sensitive.sensitivelog.annotation;

import com.kuma.boot.sensitive.sensitivelog.api.Condition;
import com.kuma.boot.sensitive.sensitivelog.api.Strategy;
import com.kuma.boot.sensitive.sensitivelog.api.impl.ConditionAlwaysTrue;
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
public @interface Sensitive {
   Class<? extends Condition> condition() default ConditionAlwaysTrue.class;

   Class<? extends Strategy> strategy();
}
