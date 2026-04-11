package com.kuma.boot.eventbus.disruptor.tmp.annotation;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.eventbus.disruptor.tmp.factory.DisruptorThreadFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ThreadFactory;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisruptorEvent {
   String value() default "";

   int bufferSize() default 262144;

   ProducerType type() default ProducerType.SINGLE;

   Class<? extends ThreadFactory> threadFactory() default DisruptorThreadFactory.class;

   String threadFactoryBeanName() default "";

   Class<? extends WaitStrategy> strategy() default BlockingWaitStrategy.class;

   String strategyBeanName() default "";
}
