package com.kuma.boot.flowengine.easywork.report;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;

public class DefaultWorkReport extends AbstractWorkReport {
   protected WorkContext workContext = new WorkContext();
   protected WorkStatus status;
   protected Throwable error;
   protected Object result;
   protected String workName;
   protected WorkStatus stoppedStatus;

   public WorkStatus getStoppedStatus() {
      return this.stoppedStatus;
   }

   public DefaultWorkReport() {
      this.status = WorkStatus.COMPLETED;
      this.error = null;
      this.result = null;
   }

   public static DefaultWorkReport aNewWorkReport() {
      return new DefaultWorkReport();
   }

   public WorkStatus getStatus() {
      return this.status;
   }

   public Throwable getError() {
      return this.error;
   }

   public WorkContext getWorkContext() {
      return this.workContext;
   }

   public Object getResult() {
      return this.result;
   }

   public String getWorkName() {
      return this.workName;
   }

   public DefaultWorkReport setWorkContext(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public DefaultWorkReport setStatus(WorkStatus status) {
      this.status = status;
      return this;
   }

   public DefaultWorkReport setError(Throwable error) {
      this.error = error;
      return this;
   }

   public DefaultWorkReport setResult(Object result) {
      this.result = result;
      return this;
   }

   public DefaultWorkReport setWorkName(String workName) {
      this.workName = workName;
      return this;
   }

   public DefaultWorkReport setStoppedStatus(WorkStatus status) {
      this.stoppedStatus = status;
      return this;
   }
}
