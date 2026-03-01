/*
 *  com.google.common.base.Preconditions
 */
package com.kuma.boot.idempotent.idempotentenhance.core.handler.event;

import com.google.common.base.Preconditions;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentExceptionEventTypeEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;

public class IdempotentExceptionEvent
extends IdempotentEvent<IdempotentEntity> {
    private final IdempotentExceptionEventTypeEnum eventType;

    public IdempotentExceptionEvent(String identifier, String source, IdempotentExceptionEventTypeEnum eventType, IdempotentEntity data) {
        super(identifier, source, data);
        this.eventType = eventType;
        Preconditions.checkNotNull((Object)((Object)eventType), (Object)"eventType can not be null.");
    }

    public IdempotentExceptionEventTypeEnum getEventType() {
        return this.eventType;
    }
}

