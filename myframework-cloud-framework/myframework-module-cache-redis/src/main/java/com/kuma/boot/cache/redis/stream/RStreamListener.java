/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.cache.redis.stream;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RStreamListener {
    public String name();

    public String group() default "";

    public MessageModel messageModel() default MessageModel.CLUSTERING;

    public ReadOffsetModel offsetModel() default ReadOffsetModel.LAST_CONSUMED;

    public boolean autoAcknowledge() default false;

    public boolean readRawBytes() default false;
}

