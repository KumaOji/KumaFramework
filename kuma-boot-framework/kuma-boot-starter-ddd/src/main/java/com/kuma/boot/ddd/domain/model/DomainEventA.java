package com.kuma.boot.ddd.domain.model;

import com.kuma.boot.ddd.model.domain.AggregateRoot;
import com.kuma.boot.ddd.model.domain.event.DefaultDomainEvent;
import java.nio.charset.StandardCharsets;

public class DomainEventA extends AggregateRoot {
   private final String eventType;
   private final String topic;
   private final String tag;
   private final Long aggregateId;
   private final String attribute;

   public DomainEventA(byte[] payload, DefaultDomainEvent domainEvent) {
      this.tenantId = domainEvent.getTenantId();
      this.creator = domainEvent.getCreator();
      this.editor = domainEvent.getEditor();
      this.eventType = domainEvent.getEventType().name();
      this.sourceName = domainEvent.getSourceName();
      this.serviceId = domainEvent.getServiceId();
      this.aggregateId = (Long)domainEvent.getAggregateId();
      this.tag = domainEvent.getTag();
      this.topic = domainEvent.getTopic();
      this.attribute = new String(payload, StandardCharsets.UTF_8);
   }

   public String getEventType() {
      return this.eventType;
   }

   public String getTopic() {
      return this.topic;
   }

   public String getTag() {
      return this.tag;
   }

   public Long getAggregateId() {
      return this.aggregateId;
   }

   public String getAttribute() {
      return this.attribute;
   }
}
