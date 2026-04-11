package com.kuma.boot.eventbus.atlas.annotation;

import com.kuma.boot.eventbus.atlas.core.Event;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventSubscribe {
   String eventType() default "";

   Class<? extends Event> eventClass() default Event.class;

   boolean async() default false;

   String threadPool() default "";
}
