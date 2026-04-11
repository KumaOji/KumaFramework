package com.kuma.boot.eventbus.disruptor.tmp5.starter;

import com.kuma.boot.eventbus.disruptor.tmp5.handler.ClearingEventHandler;
import com.kuma.boot.eventbus.disruptor.tmp5.handler.ClearingWorkHandler;
import com.kuma.boot.eventbus.disruptor.tmp5.handler.CustomExceptionHandler;
import com.kuma.boot.eventbus.disruptor.tmp5.utils.ZlfDisruptorSpringUtils;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DisruptorServiceConfiguration.class, ClearingEventHandler.class, ClearingWorkHandler.class, CustomExceptionHandler.class, ZlfDisruptorSpringUtils.class})
public @interface EnableZlfDisruptor {
}
