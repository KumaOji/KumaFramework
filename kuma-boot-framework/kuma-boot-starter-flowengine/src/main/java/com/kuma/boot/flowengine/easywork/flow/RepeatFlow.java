package com.kuma.boot.flowengine.easywork.flow;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.predicate.TimesPredicate;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.RepeatWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.function.Function;

public class RepeatFlow extends AbstractWorkFlow {
   private final WorkReportPredicate workReportPredicate;
   private boolean bePoll = false;

   private RepeatFlow(WorkReportPredicate predicate, Work theWork) {
      this.workReportPredicate = predicate;
      this.workList.add(wrapNamedPointWork(theWork));
   }

   public RepeatWorkReport execute() {
      return this.execute("");
   }

   public RepeatWorkReport execute(String point) {
      return RepeatWorkReport.aNewRepeatWorkReport(this.executeInternal(point));
   }

   public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
      if (workReport.getStatus() != WorkStatus.STOPPED && Checker.BeNotEmpty(this.thenFuns) && this.pointWork == null) {
         this.bePoll = true;
      }

      return this.executeThenInternal(workReport, point);
   }

   public void doExecute(String point) {
      if (!this.beStopped()) {
         WorkContext workContext = this.getDefaultWorkContext();
         Work work;
         if (this.bePoll) {
            work = (Work)this.queue.poll();
         } else {
            work = (Work)this.queue.peek();
         }

         WorkReport report = this.doSingleWork(work, workContext, point);
         this.multipleWorkReport.addReport(report);
         boolean beWorkFlow = work instanceof WorkFlow;
         boolean beStopped = this.beStopped();
         if (beWorkFlow) {
            if (!beStopped && this.workReportPredicate.apply(report)) {
               return;
            }
         } else if (this.workReportPredicate.apply(report)) {
            return;
         }

         if (beStopped) {
            if (beWorkFlow && this.bePoll) {
               this.queue.offerFirst(work);
            }

            this.pointWork = (Work)this.queue.peek();
         } else if (!this.beBreak(report)) {
            if (report.getStatus() != WorkStatus.STOPPED) {
               this.doExecute(point);
            }

         }
      }
   }

   public void locate2CurrentWork() {
      this.locate2CurrentWorkInternal();
   }

   public RepeatFlow then(Function<WorkReport, Work> fun) {
      this.thenFuns.add(fun);
      return this;
   }

   public RepeatFlow then(Work work) {
      this.thenFuns.add((Function)(report) -> work);
      return this;
   }

   public RepeatFlow named(String name) {
      this.name = name;
      return this;
   }

   public RepeatFlow policy(WorkExecutePolicy workExecutePolicy) {
      this.workExecutePolicy = workExecutePolicy;
      return this;
   }

   public RepeatFlow context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public RepeatFlow trace(boolean beTrace) {
      this.beTrace = beTrace;
      return this;
   }

   public static BuildSteps aNewRepeatFlow(Work theWork) {
      return new BuildSteps(theWork);
   }

   public static class BuildSteps implements UntilStep {
      private final Work innerWork;

      public BuildSteps(Work theWork) {
         this.innerWork = theWork;
      }

      public RepeatFlow until(WorkReportPredicate predicate) {
         return new RepeatFlow(predicate, this.innerWork);
      }

      public RepeatFlow times(int times) {
         return this.until(TimesPredicate.times(times));
      }
   }

   public interface UntilStep {
      RepeatFlow until(WorkReportPredicate predicate);

      RepeatFlow times(int times);
   }
}
