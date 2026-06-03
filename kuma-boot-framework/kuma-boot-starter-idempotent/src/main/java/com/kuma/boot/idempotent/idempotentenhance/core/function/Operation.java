package com.kuma.boot.idempotent.idempotentenhance.core.function;

/**
 * 操作接口
 *
 * @author wenpanfeng 2023/01/06 10:02
 */
@FunctionalInterface
public interface Operation<T> {

    /**
     * do something
     *
     * @return T
     * @throws Throwable 异常
     * @author wenpanfeng 2023/1/6 10:03
     */
    T operation() throws Throwable;

}
