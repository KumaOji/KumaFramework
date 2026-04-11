package com.kuma.boot.flowengine.easywork.flow;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;
import com.kuma.boot.flowengine.easywork.report.ConditionalWorkReport;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.work.EndWork;
import com.kuma.boot.flowengine.easywork.work.NamedConditionFalseWork;
import com.kuma.boot.flowengine.easywork.work.NamedConditionTrueWork;
import com.kuma.boot.flowengine.easywork.work.NamedConditionWork;
import com.kuma.boot.flowengine.easywork.work.NamedDecideWork;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.function.Function;

public class ConditionalFlow extends AbstractWorkFlow {
   private final WorkReportPredicate predicate;
   private WorkReport predicateWorkReport;

   private ConditionalFlow(Work theWork, WorkReportPredicate thePredicate, Work trueWork, Work falseWork) {
      this.predicate = thePredicate;
      this.workList.add(NamedDecideWork.aNewNamedDecideWork(wrapNamedPointWork(theWork)));
      this.workList.add(NamedConditionTrueWork.aNewNamedExecuteTrueWork(wrapNamedPointWork(trueWork)));
      this.workList.add(NamedConditionFalseWork.aNewNamedExecuteFalseWork(wrapNamedPointWork(falseWork)));
      this.workList.add(new EndWork());
   }

   public ConditionalWorkReport execute() {
      return this.execute("");
   }

   public ConditionalWorkReport execute(String point) {
      return ConditionalWorkReport.aNewConditionalWorkReport(this.executeInternal(point));
   }

   public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
      return this.executeThenInternal(workReport, point);
   }

   public void doExecute(String point) {
      if (!this.beStopped()) {
         WorkContext workContext = this.getDefaultWorkContext();
         Work work = (Work)this.queue.poll();
         if (!(work instanceof EndWork)) {
            WorkReport report = null;
            boolean beWorkFlow;
            if (work instanceof NamedConditionWork) {
               boolean beTrue = this.predicate.apply(this.predicateWorkReport);
               if (work instanceof NamedConditionTrueWork) {
                  NamedConditionTrueWork namedExecuteTrueWork = (NamedConditionTrueWork)work;
                  beWorkFlow = namedExecuteTrueWork.getWork() instanceof WorkFlow;
                  if (!beTrue) {
                     this.doExecute(point);
                     return;
                  }

                  report = this.doSingleWork(namedExecuteTrueWork.getWork(), workContext, point);
               } else {
                  NamedConditionFalseWork namedExecuteFalseWork = (NamedConditionFalseWork)work;
                  beWorkFlow = namedExecuteFalseWork.getWork() instanceof WorkFlow;
                  if (beTrue) {
                     this.doExecute(point);
                     return;
                  }

                  report = this.doSingleWork(namedExecuteFalseWork.getWork(), workContext, point);
               }
            } else if (work instanceof NamedDecideWork) {
               NamedDecideWork namedDecideWork = (NamedDecideWork)work;
               beWorkFlow = namedDecideWork.getDecideWork() instanceof WorkFlow;
               report = this.doSingleWork(namedDecideWork.getDecideWork(), workContext, point);
               this.predicateWorkReport = report;
            } else {
               beWorkFlow = work instanceof WorkFlow;
               report = this.doSingleWork(work, workContext, point);
            }

            this.multipleWorkReport.addReport(report);
            if (this.beStopped()) {
               if (beWorkFlow) {
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
   }

   public void locate2CurrentWork() {
      this.locate2CurrentWorkInternal();
   }

   public ConditionalFlow named(String name) {
      this.name = name;
      return this;
   }

   public ConditionalFlow policy(WorkExecutePolicy workExecutePolicy) {
      this.workExecutePolicy = workExecutePolicy;
      return this;
   }

   public ConditionalFlow context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public ConditionalFlow trace(boolean beTrace) {
      this.beTrace = beTrace;
      return this;
   }

   public ConditionalFlow then(Function<WorkReport, Work> fun) {
      this.thenFuns.add(fun);
      return this;
   }

   public ConditionalFlow then(Work work) {
      this.thenFuns.add((Function)(report) -> work);
      return this;
   }

   public static BuildSteps aNewConditionalFlow(Work work) {
      return new BuildSteps(work);
   }

   public static class BuildSteps implements WhenWork {
      private final Work work;

      public BuildSteps(Work theWork) {
         this.work = theWork;
      }

      public ConditionalFlow when(WorkReportPredicate predicate, Work trueWork, Work falseWork) {
         return new ConditionalFlow(this.work, predicate, trueWork, falseWork);
      }

      public ConditionalFlow when(WorkReportPredicate predicate, Work trueWork) {
         return new ConditionalFlow(this.work, predicate, trueWork, (Work)null);
      }
   }

   public interface WhenWork {
      ConditionalFlow when(WorkReportPredicate predicate, Work trueWork, Work falseWork);

      ConditionalFlow when(WorkReportPredicate predicate, Work trueWork);
   }
}
