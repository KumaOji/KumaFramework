package com.kuma.boot.canal.canalquick.event;

import com.alibaba.otter.canal.protocol.CanalEntry;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableEvent {
   String schemaName() default "test";

   String tableName();

   CanalEntry.EventType[] eventTypes();

   String tableComment() default "";

   String[] focusColumns() default {};
}
