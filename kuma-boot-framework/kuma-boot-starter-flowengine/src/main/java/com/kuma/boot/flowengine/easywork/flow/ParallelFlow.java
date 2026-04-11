package com.kuma.boot.flowengine.easywork.flow;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.exception.WorkFlowException;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.ParallelWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.work.EndWork;
import com.kuma.boot.flowengine.easywork.work.NamedParallelWork;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.MCffu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParallelFlow extends AbstractWorkFlow {
   private static CffuFactory cffuFactory = null;
   private static ExecutorService executor = null;
   private boolean autoShutdown = false;
   private int timeoutInSeconds = 60;

   private ParallelFlow(List<Work> works) {
      List<Work> theWorks = new ArrayList();

      for(Work work : works) {
         theWorks.add(wrapNamedPointWork(work));
      }

      this.workList.add(NamedParallelWork.aNewParallelWork(theWorks));
      this.workList.add(new EndWork());
      if (executor == null) {
         executor = this.initExecutor();
      }

      if (cffuFactory == null) {
         cffuFactory = CffuFactory.builder(executor).build();
      }

   }

   public ParallelWorkReport execute() {
      return this.execute("");
   }

   public ParallelWorkReport execute(String point) {
      ParallelWorkReport var2;
      try {
         var2 = ParallelWorkReport.aNewParallelWorkReport(this.executeInternal(point));
      } finally {
         this.shutdown();
      }

      return var2;
   }

   public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
      return this.executeThenInternal(workReport, point);
   }

   public void doExecute(String point) {
      if (!this.beStopped()) {
         WorkContext workContext = this.getDefaultWorkContext();
         Work work = (Work)this.queue.poll();
         if (!(work instanceof EndWork)) {
            WorkReport report;
            if (work instanceof NamedParallelWork) {
               report = this.doParallelWork((NamedParallelWork)work);
            } else {
               report = this.doSingleWork(work, workContext, point);
               this.multipleWorkReport.addReport(report);
            }

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

   private MultipleWorkReport doParallelWork(NamedParallelWork wrapper) {
      WorkContext context = this.getDefaultWorkContext();
      List<Work> works = wrapper.getSupplierWorks();
      List<Supplier<WorkReport>> supplierList = new ArrayList();
      works.forEach((work) -> supplierList.add((Supplier)() -> this.doSingleWork(work, context, "")));
      if (WorkExecutePolicy.FAST_SUCCESS == this.workExecutePolicy) {
         Cffu<WorkReport> report = cffuFactory.iterableOps().mSupplyAnySuccessAsync(supplierList);
         this.multipleWorkReport = this.withFastSuccessResult(report, context);
      } else if (WorkExecutePolicy.FAST_FAIL == this.workExecutePolicy) {
         MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
         this.multipleWorkReport = this.withFastFailResult(reports, context);
      } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == this.workExecutePolicy) {
         MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
         this.multipleWorkReport = this.withFastFailExceptionResult(reports, context);
      } else if (WorkExecutePolicy.FAST_ALL == this.workExecutePolicy) {
         MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
         this.multipleWorkReport = this.withFastAllResult(reports, context);
      } else if (WorkExecutePolicy.FAST_EXCEPTION == this.workExecutePolicy) {
         MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyFailFastAsync(supplierList);
         this.multipleWorkReport = this.withFastExceptionallyResult(reports, context);
      } else {
         if (WorkExecutePolicy.FAST_ALL_SUCCESS != this.workExecutePolicy) {
            throw new RuntimeException("Not support work execute policy:" + String.valueOf(this.workExecutePolicy));
         }

         MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAllSuccessAsync((Object)null, supplierList);
         this.multipleWorkReport = this.withFastAllSuccessResult(reports, context);
      }

      this.multipleWorkReport.setWorkName(this.name);
      return this.multipleWorkReport;
   }

   public ParallelFlow named(String name) {
      this.name = name;
      return this;
   }

   public ParallelFlow policy(WorkExecutePolicy workExecutePolicy) {
      this.workExecutePolicy = workExecutePolicy;
      return this;
   }

   public ParallelFlow context(WorkContext workContext) {
      this.workContext = workContext;
      return this;
   }

   public ParallelFlow trace(boolean beTrace) {
      this.beTrace = beTrace;
      return this;
   }

   public ParallelFlow then(Function<WorkReport, Work> fun) {
      this.thenFuns.add(fun);
      return this;
   }

   public ParallelFlow then(Work work) {
      this.thenFuns.add((Function)(report) -> work);
      return this;
   }

   public ParallelFlow addWork(Work work) {
      NamedParallelWork parallelWork = (NamedParallelWork)Iterables.find(this.workList, (w) -> w instanceof NamedParallelWork);
      if (parallelWork != null) {
         parallelWork.addWork(work);
      }

      return this;
   }

   private ParallelWorkReport withFastFailResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
      ParallelWorkReport workReport = new ParallelWorkReport();

      try {
         List<WorkReport> reports = (List)cffu.join((long)this.timeoutInSeconds, TimeUnit.SECONDS);
         workReport = ParallelWorkReport.aNewParallelWorkReport(this.withFastFailResult(reports, workContext));
      } catch (Exception e) {
         workReport.setError(e).setWorkContext(workContext).setStatus(WorkStatus.FAILED);
      }

      return workReport;
   }

   private ParallelWorkReport withFastFailExceptionResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
      ParallelWorkReport workReport = new ParallelWorkReport();

      try {
         List<WorkReport> reports = (List)cffu.join((long)this.timeoutInSeconds, TimeUnit.SECONDS);
         workReport = ParallelWorkReport.aNewParallelWorkReport(this.withFastFailExceptionResult(reports, workContext));
      } catch (Exception e) {
         workReport.setError(e).setWorkContext(workContext).setStatus(WorkStatus.FAILED);
      }

      return workReport;
   }

   private ParallelWorkReport withFastSuccessResult(Cffu<WorkReport> cffu, WorkContext workContext) {
      ParallelWorkReport workReport = new ParallelWorkReport();

      try {
         WorkReport report = (WorkReport)cffu.join((long)this.timeoutInSeconds, TimeUnit.SECONDS);
         workReport = ParallelWorkReport.aNewParallelWorkReport(this.withFastSuccessResult(Lists.newArrayList(new WorkReport[]{report}), workContext));
      } catch (Exception e) {
         workReport.setError(e).setWorkContext(workContext).setStatus(WorkStatus.FAILED);
      }

      return workReport;
   }

   private ParallelWorkReport withFastAllResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
      ParallelWorkReport workReport = new ParallelWorkReport();

      try {
         List<WorkReport> reports = (List)cffu.join((long)this.timeoutInSeconds, TimeUnit.SECONDS);
         workReport = ParallelWorkReport.aNewParallelWorkReport(this.withFastAllResult(reports, workContext));
      } catch (Exception e) {
         workReport.setError(e).setWorkContext(workContext).setStatus(WorkStatus.FAILED);
      }

      return workReport;
   }

   private ParallelWorkReport withFastAllSuccessResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
      ParallelWorkReport workReport = new ParallelWorkReport();

      try {
         List<WorkReport> reports = (List)cffu.join((long)this.timeoutInSeconds, TimeUnit.SECONDS);
         workReport = ParallelWorkReport.aNewParallelWorkReport(this.withFastAllResult(reports, workContext));
      } catch (Exception e) {
         workReport.setError(e).setWorkContext(workContext).setStatus(WorkStatus.FAILED);
      }

      return workReport;
   }

   private ParallelWorkReport withFastExceptionallyResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
      try {
         List<WorkReport> reports = (List)cffu.join((long)this.timeoutInSeconds, TimeUnit.SECONDS);
         ParallelWorkReport workReport = ParallelWorkReport.aNewParallelWorkReport(this.withFastExceptionallyResult(reports, workContext));
         return workReport;
      } catch (Exception e) {
         if (e instanceof WorkFlowException) {
            throw (WorkFlowException)e;
         } else {
            throw (RuntimeException)e;
         }
      }
   }

   private ThreadPoolExecutor initExecutor() {
      int corePoolSize = 10;
      int maxPoolSize = 20;
      int queueCapacity = 50;
      int keepAliveTime = 30;
      ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue(50, true);
      return new ThreadPoolExecutor(10, 20, 30L, TimeUnit.SECONDS, queue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
   }

   public static ParallelFlow aNewParallelFlow(Work... works) {
      return new ParallelFlow(Arrays.asList(works));
   }

   public ParallelFlow withExecutor(ExecutorService theExecutor) {
      if (Checker.BeNotNull(theExecutor)) {
         executor = theExecutor;
         cffuFactory = CffuFactory.builder(executor).build();
      }

      return this;
   }

   public ParallelFlow withTimeoutInSeconds(int timeoutInSeconds) {
      this.timeoutInSeconds = timeoutInSeconds;
      return this;
   }

   public ParallelFlow withAutoShutDown(boolean beAutoShutdown) {
      this.autoShutdown = beAutoShutdown;
      return this;
   }

   public void shutdown() {
      if (this.autoShutdown && executor != null) {
         executor.shutdown();
      }

   }
}
