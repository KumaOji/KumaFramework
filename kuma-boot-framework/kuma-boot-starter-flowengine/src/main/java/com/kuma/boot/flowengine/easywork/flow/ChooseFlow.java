package com.kuma.boot.flowengine.easywork.flow;

import com.google.common.collect.Lists;
import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;
import com.kuma.boot.flowengine.easywork.report.ChooseWorkReport;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.work.EndWork;
import com.kuma.boot.flowengine.easywork.work.NamedDecideWork;
import com.kuma.boot.flowengine.easywork.work.NamedOtherWiseWork;
import com.kuma.boot.flowengine.easywork.work.NamedWhenWork;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.List;
import java.util.function.Function;

public class ChooseFlow extends AbstractWorkFlow {
   private boolean shortLogic;
   private boolean beExecutedWhen;
   private boolean beExecuteThen;
   private WorkReport predicateWorkReport;

   private ChooseFlow(Work theWork, List<WhenWork> whenWorks, Work otherWiseWork) {
      this.shortLogic = Boolean.TRUE;
      this.beExecutedWhen = Boolean.FALSE;
      this.beExecuteThen = Boolean.FALSE;
      this.workList.add(NamedDecideWork.aNewNamedDecideWork(wrapNamedPointWork(theWork)));
      whenWorks.forEach((whenWork) -> this.workList.add(NamedWhenWork.aNewNamedWhenWork(whenWork.getPredicate(), wrapNamedPointWork(whenWork.getWork()))));
      this.workList.add(NamedOtherWiseWork.aNewNamedOtherWiseWork(wrapNamedPointWork(otherWiseWork)));
      this.workList.add(new EndWork());
   }

   public ChooseWorkReport execute() {
      return this.execute("");
   }

   public ChooseWorkReport execute(String point) {
      return ChooseWorkReport.aNewChooseWorkReport(this.executeInternal(point));
   }

   public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
      if (workReport.getStatus() != WorkStatus.STOPPED && Checker.BeNotEmpty(this.thenFuns) && this.pointWork == null) {
         this.beExecuteThen = true;
      }

      return this.executeThenInternal(workReport, point);
   }

   public void doExecute(String point) {
      if (!this.beStopped()) {
         WorkContext workContext = this.getDefaultWorkContext();
         Work work = (Work)this.queue.poll();
         if (!(work instanceof EndWork)) {
            boolean beWorkFlow;
            WorkReport report;
            if (work instanceof NamedWhenWork) {
               NamedWhenWork namedWhenWork = (NamedWhenWork)work;
               boolean beTrue = namedWhenWork.getPredicate().apply(this.predicateWorkReport);
               beWorkFlow = namedWhenWork.getWork() instanceof WorkFlow;
               if (!beTrue) {
                  this.doExecute(point);
                  return;
               }

               this.beExecutedWhen = true;
               report = this.doSingleWork(namedWhenWork.getWork(), workContext, point);
            } else if (work instanceof NamedDecideWork) {
               NamedDecideWork namedDecideWork = (NamedDecideWork)work;
               beWorkFlow = namedDecideWork.getDecideWork() instanceof WorkFlow;
               report = this.doSingleWork(namedDecideWork.getDecideWork(), workContext, point);
               this.predicateWorkReport = report;
            } else if (work instanceof NamedOtherWiseWork) {
               NamedOtherWiseWork namedOtherWiseWork = (NamedOtherWiseWork)work;
               beWorkFlow = namedOtherWiseWork.getWork() instanceof WorkFlow;
               if (this.beExecutedWhen) {
                  this.doExecute(point);
                  return;
               }

               report = this.doSingleWork(namedOtherWiseWork.getWork(), workContext, point);
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
            } else if (!this.shortLogic || !this.beExecutedWhen || this.beExecuteThen) {
               if (!this.beBreak(report)) {
                  if (report.getStatus() != WorkStatus.STOPPED) {
                     this.doExecute(point);
                  }

               }
            }
         }
      }
   }

   public void locate2CurrentWork() {
      this.locate2CurrentWorkInternal();
   }

   public ChooseFlow witShortLogic(boolean shortLogic) {
      this.shortLogic = shortLogic;
      return this;
   }

   public ChooseFlow named(String name) {
      this.name = name;
      return this;
   }

   public ChooseFlow policy(WorkExecutePolicy workExecutePolicy) {
      this.workExecutePolicy = workExecutePolicy;
      return this;
   }

   public ChooseFlow context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public ChooseFlow trace(boolean beTrace) {
      this.beTrace = beTrace;
      return this;
   }

   public ChooseFlow then(Function<WorkReport, Work> fun) {
      this.thenFuns.add(fun);
      return this;
   }

   public ChooseFlow then(Work work) {
      this.thenFuns.add((Function)(report) -> work);
      return this;
   }

   public static BuildSteps aNewChooseFlow(Work work) {
      return new BuildSteps(work);
   }

   private static class WhenWork {
      private WorkReportPredicate predicate;
      private Work work;

      public WhenWork(WorkReportPredicate predicate, Work work) {
         this.predicate = predicate;
         this.work = work;
      }

      public WorkReportPredicate getPredicate() {
         return this.predicate;
      }

      public void setPredicate(WorkReportPredicate predicate) {
         this.predicate = predicate;
      }

      public Work getWork() {
         return this.work;
      }

      public void setWork(Work work) {
         this.work = work;
      }
   }

   public static class BuildSteps implements ChooseWhen {
      private final List<WhenWork> innerWhenWorks = Lists.newArrayList();
      private final Work innerWork;

      public BuildSteps(Work theWork) {
         this.innerWork = AbstractWorkFlow.wrapNamedPointWork(theWork);
      }

      public ChooseWhen chooseWhen(WorkReportPredicate thePredicate, Work work) {
         this.innerWhenWorks.add(new WhenWork(thePredicate, AbstractWorkFlow.wrapNamedPointWork(work)));
         return this;
      }

      public ChooseFlow otherWise(Work work) {
         return new ChooseFlow(this.innerWork, this.innerWhenWorks, AbstractWorkFlow.wrapNamedPointWork(work));
      }
   }

   public interface ChooseWhen {
      ChooseWhen chooseWhen(WorkReportPredicate thePredicate, Work work);

      ChooseFlow otherWise(Work work);
   }
}
