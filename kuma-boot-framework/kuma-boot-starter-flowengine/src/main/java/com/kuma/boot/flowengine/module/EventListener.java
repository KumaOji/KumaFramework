package com.kuma.boot.flowengine.module;

public class EventListener {
   private String listenerClass;
   private String description;

   public EventListener(String listenerClass, String description) {
      this.listenerClass = listenerClass;
      this.description = description;
   }

   public String getListenerClass() {
      return this.listenerClass;
   }

   public String getDescription() {
      return this.description;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("EventListener{");
      sb.append("description=").append(this.description).append("");
      sb.append(", listenerClass = '").append(this.listenerClass).append("");
      sb.append("}");
      return sb.toString();
   }
}
