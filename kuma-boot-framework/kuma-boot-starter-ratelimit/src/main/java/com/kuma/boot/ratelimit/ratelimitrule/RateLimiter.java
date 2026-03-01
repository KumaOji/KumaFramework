package com.kuma.boot.ratelimit.ratelimitrule;


import com.kuma.boot.common.enums.ResultEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimiter {
    /**
     * 限流类型 ( 默认全局 )
     */
    com.kuma.boot.ratelimit.ratelimitrule.LimitTypeEnum limitTypeEnum() default com.kuma.boot.ratelimit.ratelimitrule.LimitTypeEnum.GLOBAL;

    /**
     * 对应限流规则
     */
    RateRule[] rateRules();

    /**
     * 在限流后，确定是否将请求加入黑名单 ( 用户 和 ip 都将进入黑名单 )
     */
    boolean addToBlacklist() default false;

    /**
     * 如果配置了 RateLimiters 中 message，以 RateLimiters 为准
     */
    ResultEnum message() default ResultEnum.FAILED;
}
