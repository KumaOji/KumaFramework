package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;

public class NamedOtherWiseWork extends NamedWork {
   private Work work;

   public Work getWork() {
      return this.work;
   }

   private NamedOtherWiseWork(Work work) {
      this.work = work;
   }

   public static NamedOtherWiseWork aNewNamedOtherWiseWork(Work work) {
      return new NamedOtherWiseWork(work);
   }

   public NamedOtherWiseWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      return null;
   }
}
