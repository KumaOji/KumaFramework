package com.kuma.boot.ddd.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("ttc_domain_event")
public class DomainEventDO {
   protected static final String BOOT_DOMAIN_EVENT = "ttc_domain_event";
   private String eventType;
   private String sourceName;
   private String topic;
   private String tag;
   private String serviceId;
   private Long aggregateId;
   private String attribute;

   public String getEventType() {
      return this.eventType;
   }

   public void setEventType(String eventType) {
      this.eventType = eventType;
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public void setSourceName(String sourceName) {
      this.sourceName = sourceName;
   }

   public String getTopic() {
      return this.topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public String getTag() {
      return this.tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public String getServiceId() {
      return this.serviceId;
   }

   public void setServiceId(String serviceId) {
      this.serviceId = serviceId;
   }

   public Long getAggregateId() {
      return this.aggregateId;
   }

   public void setAggregateId(Long aggregateId) {
      this.aggregateId = aggregateId;
   }

   public String getAttribute() {
      return this.attribute;
   }

   public void setAttribute(String attribute) {
      this.attribute = attribute;
   }
}
