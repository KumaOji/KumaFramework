package com.kuma.boot.canal.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListenPoint {
   String destination() default "";

   String[] schema() default {};

   String[] table() default {};

   CanalEntry.EventType[] eventType() default {};
}
