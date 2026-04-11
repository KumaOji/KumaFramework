package com.kuma.boot.eventbus.disruptor.tmp4.event;

public class DistributeEvent extends AbstractEvent {
   private String targetStation;
   private String targetTopic;
   private EventContext context;

   public DistributeEvent() {
   }

   public String getTargetStation() {
      return this.targetStation;
   }

   public void setTargetStation(String targetStation) {
      this.targetStation = targetStation;
   }

   public String getTargetTopic() {
      return this.targetTopic;
   }

   public void setTargetTopic(String targetTopic) {
      this.targetTopic = targetTopic;
   }

   public EventContext getContext() {
      return this.context;
   }

   public void setContext(EventContext context) {
      this.context = context;
   }
}
