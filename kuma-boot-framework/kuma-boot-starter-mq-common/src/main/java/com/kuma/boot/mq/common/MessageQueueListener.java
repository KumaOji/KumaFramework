package com.kuma.boot.mq.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageQueueListener {
    String type() default "";

    String group() default "";

    String topic() default "";

    int pullBatchSize() default 0;

    int consumeMessageBatchMaxSize() default 0;

    String messageModel() default "CLUSTERING";

    String selectorType() default "TAG";

    String selectorExpression() default "*";
}
