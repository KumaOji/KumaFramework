package com.kuma.boot.eventbus.anno;

import com.kuma.boot.eventbus.autoconfigure.EventBusAutoConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({EventBusAutoConfiguration.class})
@SpringBootConfiguration
public @interface EnableEventBus {
}
