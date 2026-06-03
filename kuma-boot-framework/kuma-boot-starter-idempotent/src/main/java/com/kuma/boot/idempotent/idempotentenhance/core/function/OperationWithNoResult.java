package com.kuma.boot.idempotent.idempotentenhance.core.function;

/**
 * 不带结果的操作
 *
 * @author wenpanfeng 2023/01/06 10:37
 */
@FunctionalInterface
public interface OperationWithNoResult {

    /**
     * do something
     *
     * @throws Throwable 异常
     * @author wenpanfeng 2023/1/6 10:38
     */
    void operation() throws Throwable;

}
