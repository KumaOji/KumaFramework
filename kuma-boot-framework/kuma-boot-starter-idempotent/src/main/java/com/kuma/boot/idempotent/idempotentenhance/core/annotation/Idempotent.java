package com.kuma.boot.idempotent.idempotentenhance.core.annotation;

import com.kuma.boot.idempotent.idempotentenhance.core.registry.IdempotentRepositoryRegistry;

import java.lang.annotation.*;

/**
 * 幂等注解
 *
 * @author wenpan 2022/12/31 15:06
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Idempotent {

    /**
     * 数据来源
     */
    String source() default "";

    /**
     * 操作类型
     */
    String operationType() default "";

    /**
     * 业务key
     */
    String businessKey();

    /**
     * 用于多个幂等repository实现时选择repository的关键key
     */
    String determineCurrentLookupKey() default IdempotentRepositoryRegistry.PRIMARY;

}
