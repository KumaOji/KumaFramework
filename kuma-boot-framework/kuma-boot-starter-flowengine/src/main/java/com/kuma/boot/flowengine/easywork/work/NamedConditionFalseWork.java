package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;

public class NamedConditionFalseWork extends NamedConditionWork {
   private NamedConditionFalseWork(Work falseWork) {
      this.work = falseWork;
   }

   public static NamedConditionFalseWork aNewNamedExecuteFalseWork(Work falseWork) {
      return new NamedConditionFalseWork(falseWork);
   }

   public NamedConditionFalseWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      return null;
   }
}
