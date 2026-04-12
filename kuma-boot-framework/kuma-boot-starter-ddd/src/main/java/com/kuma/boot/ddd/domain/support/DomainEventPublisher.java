package com.kuma.boot.ddd.domain.support;

import com.kuma.boot.ddd.model.domain.event.DomainEvent;

public interface DomainEventPublisher {
   void publish(DomainEvent payload, boolean isTX);
}
