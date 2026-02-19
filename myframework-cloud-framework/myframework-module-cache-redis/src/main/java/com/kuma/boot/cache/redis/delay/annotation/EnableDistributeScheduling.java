/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.cache.redis.delay.annotation;

import com.kuma.boot.cache.redis.delay.config.RedissonDelayAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Import(value={RedissonDelayAutoConfiguration.class})
@Documented
public @interface EnableDistributeScheduling {
}

