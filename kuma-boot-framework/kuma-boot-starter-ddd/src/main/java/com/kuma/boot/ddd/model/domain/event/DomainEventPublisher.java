package com.kuma.boot.ddd.model.domain.event;

public interface DomainEventPublisher {
   void publish(DomainEvent event);

   void publishAndSave(DomainEvent event);
}
