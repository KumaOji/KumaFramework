package com.kuma.boot.flowengine.easywork.report;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultipleWorkReport extends AbstractWorkReport {
   private final List<WorkReport> reports = new ArrayList();
   protected WorkContext workContext;
   protected WorkStatus status;
   protected Throwable error;
   protected String workName;

   public MultipleWorkReport() {
   }

   public List<WorkReport> getReports() {
      return this.reports;
   }

   public MultipleWorkReport setWorkContext(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public MultipleWorkReport setStatus(WorkStatus status) {
      this.status = status;
      return this;
   }

   public MultipleWorkReport setError(Throwable error) {
      this.error = error;
      return this;
   }

   public MultipleWorkReport setWorkName(String workName) {
      this.workName = workName;
      return this;
   }

   public MultipleWorkReport addAllReports(List<WorkReport> workReports) {
      this.reports.addAll(workReports);
      return this;
   }

   public MultipleWorkReport addReport(WorkReport workReport) {
      this.reports.add(workReport);
      return this;
   }

   public WorkStatus getStatus() {
      if (this.status == WorkStatus.STOPPED) {
         return WorkStatus.STOPPED;
      } else {
         boolean beStopped = this.reports.stream().anyMatch((work) -> work.getStatus().equals(WorkStatus.STOPPED));
         if (beStopped) {
            return WorkStatus.STOPPED;
         } else if (Checker.BeEmpty(this.reports) && Checker.BeNotNull(this.status)) {
            return this.status;
         } else {
            for(WorkReport report : this.reports) {
               if (report.getStatus().equals(WorkStatus.FAILED)) {
                  return WorkStatus.FAILED;
               }
            }

            return WorkStatus.COMPLETED;
         }
      }
   }

   public Throwable getError() {
      if (Checker.BeEmpty(this.reports) && Checker.BeNotNull(this.error)) {
         return this.error;
      } else {
         for(WorkReport report : this.reports) {
            Throwable error = report.getError();
            if (error != null) {
               return error;
            }
         }

         return null;
      }
   }

   public WorkContext getWorkContext() {
      if (Checker.BeEmpty(this.reports) && Checker.BeNotNull(this.workContext)) {
         return this.workContext;
      } else {
         WorkContext workContext = new WorkContext();

         for(WorkReport report : this.reports) {
            WorkContext partialContext = report.getWorkContext();

            for(Map.Entry<String, Object> entry : partialContext.getContextMap().entrySet()) {
               workContext.put((String)entry.getKey(), entry.getValue());
            }
         }

         return workContext;
      }
   }

   public String getWorkName() {
      return this.workName;
   }

   public List<Object> getResult() {
      return (List)this.reports.stream().map(WorkReport::getResult).collect(Collectors.toList());
   }

   public <T> T getResult(int index, Class<T> clazz) {
      return (T)this.getResult().get(index);
   }

   public <T> Collection<T> getResultCollection(int index, Class<T> clazz) {
      return (Collection)this.getResult(index, clazz);
   }

   public void copy(MultipleWorkReport source) {
      this.setStatus(source.getStatus());
      this.setWorkName(source.getWorkName());
      this.setWorkContext(source.getWorkContext());
      this.setError(source.getError());
      this.addAllReports(source.getReports());
   }
}
