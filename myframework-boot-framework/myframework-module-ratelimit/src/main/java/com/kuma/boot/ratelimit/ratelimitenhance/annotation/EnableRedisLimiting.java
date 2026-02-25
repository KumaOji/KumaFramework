/*
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.ratelimit.ratelimitenhance.annotation;

import com.kuma.boot.ratelimit.ratelimitenhance.selector.RedisLimitingImportSelector;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Import(value={RedisLimitingImportSelector.class})
public @interface EnableRedisLimiting {
}

