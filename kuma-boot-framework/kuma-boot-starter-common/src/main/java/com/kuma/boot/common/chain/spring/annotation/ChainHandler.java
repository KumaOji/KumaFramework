package com.kuma.boot.common.chain.spring.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChainHandler {

    /** 链的 ID */
    String value();

    /** 处理者在链中的顺序，数值越小优先级越高 */
    int order() default 0;
}
