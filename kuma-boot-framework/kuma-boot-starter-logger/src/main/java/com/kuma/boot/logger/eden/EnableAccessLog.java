package com.kuma.boot.logger.eden;

import com.kuma.boot.logger.eden.access.config.AccessLogImportSelector;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

@Import({AccessLogImportSelector.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableAccessLog {
   boolean proxyTargetClass() default false;

   AdviceMode mode() default AdviceMode.PROXY;

   int order() default Integer.MAX_VALUE;

   String expression() default "";
}
