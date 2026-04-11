package com.kuma.boot.flowengine.easywork.flow;

import com.google.common.collect.Iterables;
import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;
import com.kuma.boot.flowengine.easywork.report.LoopIndexWorkReport;
import com.kuma.boot.flowengine.easywork.report.LoopWorkReport;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class LoopFlow extends AbstractWorkFlow {
   private final LoopIndexWorkReport indexReport = new LoopIndexWorkReport();
   private WorkReportPredicate breakPredicate;
   private WorkReportPredicate continuePredicate;
   private int index = 0;
   private boolean bePoll = false;

   private LoopFlow(List<Work> works) {
      works.forEach((work) -> this.workList.add(wrapNamedPointWork(work)));
      this.indexReport.setLength(works.size());
   }

   public LoopWorkReport execute() {
      return this.execute("");
   }

   public LoopWorkReport execute(String point) {
      return LoopWorkReport.aNewLoopWorkReport(this.executeInternal(point));
   }

   public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
      if (workReport.getStatus() != WorkStatus.STOPPED && Checker.BeNotEmpty(this.thenFuns) && this.pointWork == null) {
         this.bePoll = true;
      }

      return this.executeThenInternal(workReport, point);
   }

   public void doExecute(String point) {
      if (!this.beStopped()) {
         this.indexReport.setIndex(this.index);
         if (this.bePoll || !Checker.BeNotNull(this.breakPredicate) || !this.breakPredicate.apply(this.indexReport)) {
            if (!this.bePoll && Checker.BeNotNull(this.continuePredicate) && this.continuePredicate.apply(this.indexReport)) {
               Work work = (Work)this.queue.poll();
               if (!this.bePoll) {
                  this.queue.offer(work);
               }
            }

            WorkContext workContext = this.getDefaultWorkContext();
            Work work = (Work)this.queue.poll();
            if (!this.bePoll) {
               this.queue.offer(work);
            }

            WorkReport report = this.doSingleWork(work, workContext, point);
            this.multipleWorkReport.addReport(report);
            this.indexReport.with(report);
            ++this.index;
            if (this.beStopped()) {
               if (work instanceof WorkFlow) {
                  if (!this.bePoll) {
                     this.queue.removeLast();
                  }

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
      if (Checker.BeNull(this.pointWork)) {
         this.queue = new LinkedList();
         this.queue.addAll(this.workList);
      } else {
         if (this.bePoll) {
            this.queue = new LinkedList();
            String currentWorkName = this.getNameOfWork(this.pointWork);
            int index = Iterables.indexOf(this.workList, (w) -> currentWorkName.equals(this.getNameOfWork(w)));
            this.queue.addAll(this.workList.subList(index, this.workList.size()));
         }

         this.pointWork = null;
      }
   }

   public LoopFlow named(String name) {
      this.name = name;
      return this;
   }

   public LoopFlow policy(WorkExecutePolicy workExecutePolicy) {
      this.workExecutePolicy = workExecutePolicy;
      return this;
   }

   public LoopFlow context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public LoopFlow trace(boolean beTrace) {
      this.beTrace = beTrace;
      return this;
   }

   public LoopFlow then(Function<WorkReport, Work> fun) {
      this.thenFuns.add(fun);
      return this;
   }

   public LoopFlow then(Work work) {
      this.thenFuns.add((Function)(report) -> work);
      return this;
   }

   public LoopFlow addWork(Work work) {
      this.workList.add(work);
      return this;
   }

   public LoopFlow addWork(int index, Work work) {
      int enIndex = this.workList.size() - 1;
      if (index < 0) {
         index = 0;
      }

      if (index > enIndex) {
         index = enIndex;
      }

      this.workList.add(index, work);
      return this;
   }

   public static LoopFlow aNewLoopFlow(Work... works) {
      return new LoopFlow(Arrays.asList(works));
   }

   public LoopFlow withBreakPredicate(WorkReportPredicate breakPredicate) {
      this.breakPredicate = breakPredicate;
      return this;
   }

   public LoopFlow withContinuePredicate(WorkReportPredicate continuePredicate) {
      this.continuePredicate = continuePredicate;
      return this;
   }
}
