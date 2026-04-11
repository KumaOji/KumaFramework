package com.kuma.boot.flowengine.easywork.work;

import java.util.UUID;

public abstract class NamedWork implements Work {
   protected String name = UUID.randomUUID().toString();

   public NamedWork() {
   }

   public String getName() {
      return this.name;
   }

   public abstract NamedWork named(String name);
}
