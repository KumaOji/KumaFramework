package com.kuma.boot.flowengine.easywork.report;

import java.util.List;

public class ConditionalWorkReport extends MultipleWorkReport {
   public ConditionalWorkReport() {
   }

   public static ConditionalWorkReport aNewConditionalWorkReport() {
      return new ConditionalWorkReport();
   }

   public static ConditionalWorkReport aNewConditionalWorkReport(MultipleWorkReport workReport) {
      ConditionalWorkReport report = new ConditionalWorkReport();
      report.copy(workReport);
      return report;
   }

   public ConditionalWorkReport addAllReports(List<WorkReport> workReports) {
      this.getReports().addAll(workReports);
      return this;
   }

   public ConditionalWorkReport addReport(WorkReport workReport) {
      this.getReports().add(workReport);
      return this;
   }

   public ConditionalWorkReport setWorkName(String workName) {
      this.workName = workName;
      return this;
   }
}
