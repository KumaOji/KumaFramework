package com.kuma.boot.flowengine.easywork.predicate;

import com.kuma.boot.flowengine.easywork.report.LoopIndexWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;

public class LoopIndexPredicate implements WorkReportPredicate {
   private int index = 0;

   public LoopIndexPredicate(int index) {
      this.index = index;
   }

   public static LoopIndexPredicate indexPredicate(int index) {
      return new LoopIndexPredicate(index);
   }

   public boolean apply(WorkReport workReport) {
      if (Checker.BeNotNull(workReport) && workReport instanceof LoopIndexWorkReport loopWorkReport) {
         return loopWorkReport.getIndex() == this.index;
      } else {
         return false;
      }
   }
}
