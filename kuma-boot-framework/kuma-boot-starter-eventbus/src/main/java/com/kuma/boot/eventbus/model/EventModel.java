package com.kuma.boot.eventbus.model;

import java.io.Serializable;

public class EventModel<T> implements Serializable {
   private String topic;
   private T entity;

   public EventModel() {
   }

   public String getTopic() {
      return this.topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public T getEntity() {
      return this.entity;
   }

   public void setEntity(T entity) {
      this.entity = entity;
   }
}
