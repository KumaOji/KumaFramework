package com.kuma.boot.ddd.model.domain.event;

import com.kuma.boot.ddd.model.domain.AggregateRoot;
import java.time.Instant;

public class DefaultDomainEvent extends DomainEvent {
   protected DefaultDomainEvent(AggregateRoot aggregateRoot, String topic, String tag, EventType eventType, Instant instant) {
      this.generatorId();
   }

   protected DefaultDomainEvent(String topic, String tag, EventType eventType, String serviceId, String sourceName, Instant instant, Long aggregateId, Long tenantId) {
      this.generatorId();
      super.aggregateId = aggregateId;
      super.eventType = eventType;
      super.tag = tag;
      super.topic = topic;
   }

   protected DefaultDomainEvent(String topic, String tag) {
      super.topic = topic;
      super.tag = tag;
   }

   public DefaultDomainEvent(String serviceId) {
      super.serviceId = serviceId;
   }

   protected void generatorId() {
      super.id = System.currentTimeMillis();
   }
}
