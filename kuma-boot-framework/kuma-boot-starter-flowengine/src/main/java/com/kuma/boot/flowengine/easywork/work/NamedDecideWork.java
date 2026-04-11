package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;

public class NamedDecideWork extends NamedWork {
   private final Work decideWork;

   public Work getDecideWork() {
      return this.decideWork;
   }

   private NamedDecideWork(Work work) {
      this.decideWork = work;
   }

   public static NamedDecideWork aNewNamedDecideWork(Work work) {
      return new NamedDecideWork(work);
   }

   public NamedDecideWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      return null;
   }
}
