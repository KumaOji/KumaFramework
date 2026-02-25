/*
 *  com.kuma.boot.common.enums.ResultEnum
 */
package com.kuma.boot.ratelimit.ratelimitrule;

import com.kuma.boot.common.enums.ResultEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimiter {
    public LimitTypeEnum limitTypeEnum() default LimitTypeEnum.GLOBAL;

    public RateRule[] rateRules();

    public boolean addToBlacklist() default false;

    public ResultEnum message() default ResultEnum.FAILED;
}

