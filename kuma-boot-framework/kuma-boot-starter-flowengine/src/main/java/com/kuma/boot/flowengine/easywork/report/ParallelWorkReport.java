package com.kuma.boot.flowengine.easywork.report;

import java.util.List;

public class ParallelWorkReport extends MultipleWorkReport {
   public ParallelWorkReport() {
   }

   public static ParallelWorkReport aNewParallelWorkReport() {
      return new ParallelWorkReport();
   }

   public static ParallelWorkReport aNewParallelWorkReport(MultipleWorkReport workReport) {
      ParallelWorkReport report = new ParallelWorkReport();
      report.copy(workReport);
      return report;
   }

   public ParallelWorkReport addAllReports(List<WorkReport> workReports) {
      this.getReports().addAll(workReports);
      return this;
   }

   public ParallelWorkReport addReport(WorkReport workReport) {
      this.getReports().add(workReport);
      return this;
   }

   public ParallelWorkReport setWorkName(String workName) {
      this.workName = workName;
      return this;
   }
}
