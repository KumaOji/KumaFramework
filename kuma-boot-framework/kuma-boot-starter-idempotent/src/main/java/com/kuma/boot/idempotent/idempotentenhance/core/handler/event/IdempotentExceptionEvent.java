package com.kuma.boot.idempotent.idempotentenhance.core.handler.event;

import com.google.common.base.Preconditions;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentExceptionEventTypeEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;

/**
 * 幂等异常事件
 *
 * @author wenpan 2023/01/07 13:03
 */
public class IdempotentExceptionEvent extends IdempotentEvent<IdempotentEntity> {

    /**
     * 幂等异常事件类型
     */
    private final IdempotentExceptionEventTypeEnum eventType;

    public IdempotentExceptionEvent(String identifier,
                                    String source,
                                    IdempotentExceptionEventTypeEnum eventType,
                                    IdempotentEntity data) {
        super(identifier, source, data);
        this.eventType = eventType;
        Preconditions.checkNotNull(eventType, "eventType can not be null.");
    }

    public IdempotentExceptionEventTypeEnum getEventType() {
        return eventType;
    }


}
