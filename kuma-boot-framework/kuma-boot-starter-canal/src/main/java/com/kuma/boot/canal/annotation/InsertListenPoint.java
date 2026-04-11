package com.kuma.boot.canal.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ListenPoint(
   eventType = {EventType.INSERT}
)
public @interface InsertListenPoint {
   @AliasFor(
      annotation = ListenPoint.class
   )
   String destination() default "";

   @AliasFor(
      annotation = ListenPoint.class
   )
   String[] schema() default {};

   @AliasFor(
      annotation = ListenPoint.class
   )
   String[] table() default {};
}
