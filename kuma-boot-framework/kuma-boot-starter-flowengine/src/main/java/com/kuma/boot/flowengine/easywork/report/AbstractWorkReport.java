package com.kuma.boot.flowengine.easywork.report;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.flow.ConditionalFlow;
import com.kuma.boot.flowengine.easywork.flow.LoopFlow;
import com.kuma.boot.flowengine.easywork.flow.ParallelFlow;
import com.kuma.boot.flowengine.easywork.flow.RepeatFlow;
import com.kuma.boot.flowengine.easywork.flow.SequentialFlow;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;
import com.kuma.boot.flowengine.easywork.work.Work;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public abstract class AbstractWorkReport implements WorkReport {
   protected WorkContext workContext = new WorkContext();

   public AbstractWorkReport() {
   }

   public WorkReport context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public WorkReport execute(WorkContext context) {
      return this;
   }

   public WorkReport thenExecute(Work... works) {
      return SequentialFlow.aNewSequentialFlow(works).context(this.workContext).execute();
   }

   public WorkReport thenExecute(Function<WorkReport, Work> fn) {
      return SequentialFlow.aNewSequentialFlow((Work)fn.apply(this)).context(this.workContext).execute();
   }

   public WorkReport whenExecute(WorkReportPredicate predicate, Work work) {
      return ConditionalFlow.aNewConditionalFlow(this).when(predicate, work).context(this.workContext).execute();
   }

   public WorkReport whenExecute(WorkReportPredicate predicate, Work trueWork, Work falseWork) {
      return ConditionalFlow.aNewConditionalFlow(this).when(predicate, trueWork, falseWork).context(this.workContext).execute();
   }

   public WorkReport repatUtilExecute(WorkReportPredicate predicate, Work work) {
      return RepeatFlow.aNewRepeatFlow(work).until(predicate).context(this.workContext).execute();
   }

   public WorkReport parallelExecute(ExecutorService service, Work... works) {
      return ParallelFlow.aNewParallelFlow(works).withExecutor(service).context(this.workContext).execute();
   }

   public WorkReport parallelExecute(Work... works) {
      return ParallelFlow.aNewParallelFlow(works).context(this.workContext).execute();
   }

   public WorkReport loopExecute(Work... works) {
      return LoopFlow.aNewLoopFlow(works).context(this.workContext).execute();
   }

   public WorkReport loopExecute(WorkReportPredicate breakPredicate, WorkReportPredicate continuePredicate, Work... works) {
      return LoopFlow.aNewLoopFlow(works).withBreakPredicate(breakPredicate).withContinuePredicate(continuePredicate).context(this.workContext).execute();
   }
}
