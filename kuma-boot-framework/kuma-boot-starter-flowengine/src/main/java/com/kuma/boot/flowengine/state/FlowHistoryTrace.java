package com.kuma.boot.flowengine.state;

import java.util.Date;
import java.util.UUID;

public class FlowHistoryTrace extends FlowTrace {
   private Date endTime;
   private String error;

   public FlowHistoryTrace() {
      this(UUID.randomUUID().toString());
   }

   public FlowHistoryTrace(String eventId) {
      this(eventId, new Date());
   }

   public FlowHistoryTrace(String eventId, Date eventTime) {
      super(eventId, eventTime);
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public void setEndTime(Date endTime) {
      this.endTime = endTime;
   }

   public String getError() {
      return this.error;
   }

   public void setError(String error) {
      this.error = error;
   }
}
