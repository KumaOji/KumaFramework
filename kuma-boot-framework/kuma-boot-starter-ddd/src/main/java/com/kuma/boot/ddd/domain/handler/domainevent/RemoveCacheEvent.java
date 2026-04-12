package com.kuma.boot.ddd.domain.handler.domainevent;

import com.kuma.boot.ddd.model.domain.event.DefaultDomainEvent;

public class RemoveCacheEvent extends DefaultDomainEvent {
   private String name;
   private String key;

   public RemoveCacheEvent(String name, String key) {
      super("laokou_cache_topic", "");
      this.name = name;
      this.key = key;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }
}
