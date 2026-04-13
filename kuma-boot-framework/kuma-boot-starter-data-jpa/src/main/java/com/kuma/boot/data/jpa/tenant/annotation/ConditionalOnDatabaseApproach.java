package com.kuma.boot.data.jpa.tenant.annotation;

import com.kuma.boot.data.jpa.tenant.condition.DatabaseApproachCondition;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional({DatabaseApproachCondition.class})
public @interface ConditionalOnDatabaseApproach {
}
