package com.kuma.boot.flowengine.easywork.predicate;

import com.kuma.boot.flowengine.easywork.report.LoopIndexWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;

public class LoopLengthPredicate implements WorkReportPredicate {
   private int length = 0;

   public LoopLengthPredicate(int totalLength) {
      this.length = totalLength;
   }

   public static LoopIndexPredicate lengthPredicate(int length) {
      return new LoopIndexPredicate(length);
   }

   public boolean apply(WorkReport workReport) {
      if (Checker.BeNotNull(workReport) && workReport instanceof LoopIndexWorkReport loopWorkReport) {
         return loopWorkReport.getLength() == this.length;
      } else {
         return false;
      }
   }
}
