package com.kuma.boot.test.junitperf.support.statements;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kuma.boot.common.utils.thread.ThreadUtils;
import com.kuma.boot.test.junitperf.core.report.Reporter;
import com.kuma.boot.test.junitperf.core.statistics.StatisticsCalculator;
import com.kuma.boot.test.junitperf.model.evaluation.EvaluationContext;
import com.kuma.boot.test.junitperf.model.evaluation.component.EvaluationConfig;
import com.kuma.boot.test.junitperf.support.exception.JunitPerfRuntimeException;
import com.kuma.boot.test.junitperf.support.i18n.I18N;
import com.kuma.boot.test.junitperf.support.task.PerformanceEvaluationTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public class PerformanceEvaluationStatement {
   private static final String THREAD_NAME_PATTERN = "performance-evaluation-thread-%d";
   private static final ThreadFactory FACTORY = (new ThreadFactoryBuilder()).setNameFormat("performance-evaluation-thread-%d").build();
   private final EvaluationContext evaluationContext;
   private final StatisticsCalculator statisticsCalculator;
   private final Set<Reporter> reporterSet;
   private final Collection<EvaluationContext> evaluationContextList;
   private final Class testClass;

   public PerformanceEvaluationStatement(EvaluationContext evaluationContext, StatisticsCalculator statisticsCalculator, Set<Reporter> reporterSet, Collection<EvaluationContext> evaluationContextList, final Class testClass) {
      this.evaluationContext = evaluationContext;
      this.statisticsCalculator = statisticsCalculator;
      this.reporterSet = reporterSet;
      this.evaluationContextList = evaluationContextList;
      this.testClass = testClass;
   }

   public void evaluate() throws Throwable {
      List<PerformanceEvaluationTask> taskList = new LinkedList<>();

      try {
         EvaluationConfig evaluationConfig = this.evaluationContext.getEvaluationConfig();

         for(int i = 0; i < evaluationConfig.getConfigThreads(); ++i) {
            PerformanceEvaluationTask task = new PerformanceEvaluationTask(evaluationConfig.getConfigWarmUp(), this.statisticsCalculator, this.evaluationContext.getTestInstance(), this.evaluationContext.getTestMethod());
            Thread t = FACTORY.newThread(task);
            taskList.add(task);
            t.start();
         }

         Thread.sleep(evaluationConfig.getConfigDuration());
      } finally {
         for(PerformanceEvaluationTask task : taskList) {
            task.setContinue(false);
         }

      }

      this.evaluationContext.setStatisticsCalculator(this.statisticsCalculator);
      this.evaluationContext.runValidation();
      this.generateReporter();
   }

   private synchronized void generateReporter() {
      if (this.reporterSet.isEmpty()) {
         String var1 = I18N.get("reportIsEmpty");
      }

      int bestThreadNum = ThreadUtils.bestThreadNum(this.reporterSet.size());
      if (bestThreadNum <= 1) {
         Reporter reporter = (Reporter)this.reporterSet.iterator().next();
         reporter.report(this.testClass, this.evaluationContextList);
      } else {
         ExecutorService executorService = Executors.newFixedThreadPool(bestThreadNum);
         List<Future<Void>> futureTasks = new ArrayList<>();

         for(Reporter reporter : this.reporterSet) {
            Callable<Void> tocGenCallable = () -> {
               reporter.report(this.testClass, this.evaluationContextList);
               return null;
            };
            Future<Void> reporterFuture = executorService.submit(tocGenCallable);
            futureTasks.add(reporterFuture);
         }

         executorService.shutdown();

         try {
            for(Future<Void> reporterFuture : futureTasks) {
               Void var13 = (Void)reporterFuture.get();
            }
         } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JunitPerfRuntimeException(e);
         }
      }

   }
}
