package com.kuma.boot.flowengine.easywork.flow;

import com.google.common.collect.Iterables;
import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.SequentialWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.work.EndWork;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SequentialFlow extends AbstractWorkFlow {
   private SequentialFlow(List<Work> works) {
      works.forEach((work) -> this.workList.add(wrapNamedPointWork(work)));
      this.workList.add(new EndWork());
   }

   public SequentialWorkReport execute() {
      return this.execute("");
   }

   public SequentialWorkReport execute(String point) {
      return SequentialWorkReport.aNewSequentialWorkReport(this.executeInternal(point));
   }

   public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
      return this.executeThenInternal(workReport, point);
   }

   public void doExecute(String point) {
      if (!this.beStopped()) {
         WorkContext workContext = this.getDefaultWorkContext();
         Work work = (Work)this.queue.poll();
         if (!(work instanceof EndWork)) {
            WorkReport report = this.doSingleWork(work, workContext, point);
            this.multipleWorkReport.addReport(report);
            if (this.beStopped()) {
               if (work instanceof WorkFlow) {
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

   public SequentialFlow then(Function<WorkReport, Work> fun) {
      this.thenFuns.add(fun);
      return this;
   }

   public SequentialFlow then(Work work) {
      this.thenFuns.add((Function)(report) -> work);
      return this;
   }

   public static SequentialFlow aNewSequentialFlow(Work... works) {
      return new SequentialFlow(Arrays.asList(works));
   }

   public SequentialFlow addWork(Work work) {
      int index = Iterables.indexOf(this.workList, (w) -> w instanceof EndWork);
      this.workList.add(index, work);
      return this;
   }

   public SequentialFlow addWork(int index, Work work) {
      int endWorkIndex = Iterables.indexOf(this.workList, (w) -> w instanceof EndWork);
      if (index < 0) {
         index = 0;
      }

      if (index > endWorkIndex) {
         index = endWorkIndex;
      }

      this.workList.add(index, work);
      return this;
   }

   public SequentialFlow named(String name) {
      this.name = name;
      return this;
   }

   public SequentialFlow policy(WorkExecutePolicy workExecutePolicy) {
      this.workExecutePolicy = workExecutePolicy;
      return this;
   }

   public SequentialFlow context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public SequentialFlow trace(boolean beTrace) {
      this.beTrace = beTrace;
      return this;
   }
}
