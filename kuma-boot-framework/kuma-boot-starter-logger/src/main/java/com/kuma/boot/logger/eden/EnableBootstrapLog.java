package com.kuma.boot.logger.eden;

import com.kuma.boot.logger.eden.bootstrap.config.BootstrapLogConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Import({BootstrapLogConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableBootstrapLog {
}
