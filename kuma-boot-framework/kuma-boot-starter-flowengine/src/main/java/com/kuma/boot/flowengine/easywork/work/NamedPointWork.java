package com.kuma.boot.flowengine.easywork.work;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.listener.WorkExecuteListener;

public class NamedPointWork extends NamedWork {
   private final Work work;
   private WorkExecuteListener workExecuteListener;
   private String point;
   private boolean beExecuted = true;

   public Work getWork() {
      return this.work;
   }

   public WorkExecuteListener getWorkExecuteListener() {
      return this.workExecuteListener;
   }

   public String getPoint() {
      return this.point;
   }

   public boolean isBeExecuted() {
      return this.beExecuted;
   }

   public static NamedPointWork aNamePointWork(Work work) {
      return new NamedPointWork(work);
   }

   public NamedPointWork(Work work) {
      this.work = work;
   }

   public NamedPointWork point(String point) {
      this.point = point;
      return this;
   }

   public NamedPointWork named(String name) {
      this.name = name;
      return this;
   }

   public Object execute(WorkContext context) {
      this.beExecuted = true;
      return this.work.execute(context);
   }

   public NamedPointWork addWorkExecuteListener(WorkExecuteListener workExecuteListener) {
      this.workExecuteListener = workExecuteListener;
      return this;
   }
}
