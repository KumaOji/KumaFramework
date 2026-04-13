package com.kuma.boot.data.jpa.simplestjpa.annotation;

import com.kuma.boot.data.jpa.simplestjpa.config.BeanAutoConfiguration;
import com.kuma.boot.data.jpa.simplestjpa.config.JpaConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({BeanAutoConfiguration.class, JpaConfig.class})
public @interface EnableJPAQuery {
}
