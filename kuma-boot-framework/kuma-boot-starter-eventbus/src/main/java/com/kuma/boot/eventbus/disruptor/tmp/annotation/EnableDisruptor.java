package com.kuma.boot.eventbus.disruptor.tmp.annotation;

import com.kuma.boot.eventbus.disruptor.tmp.DisruptorRegistrar;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({DisruptorRegistrar.class})
public @interface EnableDisruptor {
   @AliasFor("basePackages")
   String[] value() default {};

   @AliasFor("value")
   String[] basePackages() default {};

   Class<?>[] basePackageClasses() default {};
}
