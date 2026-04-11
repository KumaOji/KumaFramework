package com.kuma.boot.flowengine.easywork.step;

import com.kuma.boot.flowengine.easywork.flow.WorkFlow;
import com.kuma.boot.flowengine.easywork.predicate.WorkReportPredicate;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.work.Work;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public interface ExecuteStep extends WorkFlow {
   WorkReport loopExecute(Work... works);

   WorkReport loopExecute(WorkReportPredicate breakPredicate, WorkReportPredicate continuePredicate, Work... works);

   WorkReport parallelExecute(ExecutorService service, Work... works);

   WorkReport parallelExecute(Work... works);

   WorkReport repatUtilExecute(WorkReportPredicate predicate, Work work);

   WorkReport thenExecute(Work... works);

   WorkReport thenExecute(Function<WorkReport, Work> fn);

   WorkReport whenExecute(WorkReportPredicate predicate, Work work);

   WorkReport whenExecute(WorkReportPredicate predicate, Work trueWork, Work falseWork);
}
