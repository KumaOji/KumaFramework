package com.kuma.boot.idempotent.idempotentenhance.core.handler;

import com.kuma.boot.idempotent.idempotentenhance.core.handler.event.IdempotentExceptionEvent;

/**
 * 幂等异常事件处理器
 *
 * @author wenpan 2023/01/07 13:02
 */
public interface IdempotentExceptionEventHandler {

    /**
     * <p>
     * 处理幂等异常事件, 最好是异步执行或者不影响业务执行流程执行，尽量将事件保存下来
     * </p>
     *
     * @param exceptionEvent 幂等异常事件
     */
    void handle(IdempotentExceptionEvent exceptionEvent);
}
