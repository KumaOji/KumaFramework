/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.mq.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MessageQueueListener {
    public String type() default "";

    public String group() default "";

    public String topic() default "";

    public int pullBatchSize() default 0;

    public int consumeMessageBatchMaxSize() default 0;

    public String messageModel() default "CLUSTERING";

    public String selectorType() default "TAG";

    public String selectorExpression() default "*";
}

