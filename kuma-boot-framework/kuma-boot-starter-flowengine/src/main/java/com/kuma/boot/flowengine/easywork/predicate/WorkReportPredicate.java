package com.kuma.boot.flowengine.easywork.predicate;

import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;

@FunctionalInterface
public interface WorkReportPredicate {
   WorkReportPredicate ALWAYS_TRUE = (workReport) -> true;
   WorkReportPredicate ALWAYS_FALSE = (workReport) -> false;
   WorkReportPredicate COMPLETED = (workReport) -> workReport.getStatus().equals(WorkStatus.COMPLETED);
   WorkReportPredicate FAILED = (workReport) -> workReport.getStatus().equals(WorkStatus.FAILED);

   boolean apply(WorkReport workReport);
}
