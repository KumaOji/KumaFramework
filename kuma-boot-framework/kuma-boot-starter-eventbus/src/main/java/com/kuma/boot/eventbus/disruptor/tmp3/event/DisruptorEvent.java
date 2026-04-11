package com.kuma.boot.eventbus.disruptor.tmp3.event;

import java.util.EventObject;

public abstract class DisruptorEvent extends EventObject {
   private final long timestamp = System.currentTimeMillis();
   private String event;
   private String tag;
   private String key;
   private Object body;

   public DisruptorEvent(Object source) {
      super(source);
   }

   public final long getTimestamp() {
      return this.timestamp;
   }

   public String getRouteExpression() {
      return "/" + this.getEvent() + "/" + this.getTag() + "/" + this.getKey();
   }

   public void setSource(Object source) {
      this.source = source;
   }

   public String getEvent() {
      return this.event;
   }

   public void setEvent(String event) {
      this.event = event;
   }

   public String getTag() {
      return this.tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public Object getBody() {
      return this.body;
   }

   public void setBody(Object body) {
      this.body = body;
   }

   public String toString() {
      return "DisruptorEvent [event :" + this.getEvent() + ",tag :" + this.getTag() + ", key :" + this.getKey() + "]";
   }
}
