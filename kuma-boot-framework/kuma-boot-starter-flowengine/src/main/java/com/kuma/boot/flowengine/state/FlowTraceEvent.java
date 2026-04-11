package com.kuma.boot.flowengine.state;

import com.google.common.collect.Maps;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class FlowTraceEvent {
   private String eventId;
   private Date eventTime;
   private Map<String, String> context;

   public FlowTraceEvent() {
      this(UUID.randomUUID().toString());
   }

   public FlowTraceEvent(String eventId) {
      this(eventId, new Date());
   }

   public FlowTraceEvent(String eventId, Date eventTime) {
      this.context = Maps.newHashMap();
      this.eventId = eventId;
      this.eventTime = eventTime;
   }

   public String getEventId() {
      return this.eventId;
   }

   public Date getEventTime() {
      return this.eventTime;
   }

   public void setEventId(String eventId) {
      this.eventId = eventId;
   }

   public void setEventTime(Date eventTime) {
      this.eventTime = eventTime;
   }

   public Map<String, String> getContext() {
      return this.context;
   }

   public void setContext(Map<String, String> context) {
      this.context = context;
   }
}
