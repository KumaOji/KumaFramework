package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;

public class NamedConditionTrueWork extends NamedConditionWork {
   private NamedConditionTrueWork(Work trueWork) {
      this.work = trueWork;
   }

   public static NamedConditionTrueWork aNewNamedExecuteTrueWork(Work trueWork) {
      return new NamedConditionTrueWork(trueWork);
   }

   public NamedConditionTrueWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      return null;
   }
}
