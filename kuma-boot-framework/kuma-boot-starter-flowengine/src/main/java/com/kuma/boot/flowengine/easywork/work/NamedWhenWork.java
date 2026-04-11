package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;

public class NamedWhenWork extends NamedWork {
   private final WorkReportPredicate predicate;
   private final Work work;

   public WorkReportPredicate getPredicate() {
      return this.predicate;
   }

   public Work getWork() {
      return this.work;
   }

   private NamedWhenWork(WorkReportPredicate predicate, Work work) {
      this.predicate = predicate;
      this.work = work;
   }

   public static NamedWhenWork aNewNamedWhenWork(WorkReportPredicate predicate, Work work) {
      return new NamedWhenWork(predicate, work);
   }

   public NamedWhenWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      return null;
   }
}
