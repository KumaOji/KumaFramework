package com.kuma.boot.flowengine.easywork.flow;

import com.google.common.collect.Iterables;
import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.exception.WorkFlowException;
import com.kuma.boot.flowengine.easywork.listener.WorkExecuteListener;
import com.kuma.boot.flowengine.easywork.report.DefaultWorkReport;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.ParallelWorkReport;
import com.kuma.boot.flowengine.easywork.report.SequentialWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.step.LastStep;
import com.kuma.boot.flowengine.easywork.step.ThenStep;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.work.NamedPointWork;
import com.kuma.boot.flowengine.easywork.work.NamedWork;
import com.kuma.boot.flowengine.easywork.work.Work;
import com.kuma.boot.flowengine.easywork.work.WorkExecutePolicy;
import com.kuma.boot.flowengine.easywork.work.WorkStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractWorkFlow implements ThenStep, LastStep, PointWorkFlow {
   protected String name = UUID.randomUUID().toString();
   protected WorkExecutePolicy workExecutePolicy;
   protected WorkContext workContext;
   protected Boolean beTrace;
   protected String currentPoint;
   protected final List<Function<WorkReport, Work>> thenFuns;
   protected Work lastWork;
   protected final MultipleWorkReport executedReport;
   protected final Map<String, WorkReport> executedReportMap;
   protected final List<Work> workList;
   protected Work pointWork;
   protected LinkedList<Work> queue;
   protected MultipleWorkReport multipleWorkReport;

   public AbstractWorkFlow() {
      this.workExecutePolicy = WorkExecutePolicy.FAST_FAIL_EXCEPTION;
      this.workContext = null;
      this.beTrace = Boolean.FALSE;
      this.currentPoint = null;
      this.thenFuns = new ArrayList();
      this.executedReport = new MultipleWorkReport();
      this.executedReportMap = new HashMap();
      this.workList = new ArrayList();
      this.multipleWorkReport = SequentialWorkReport.aNewSequentialWorkReport();
   }

   public abstract MultipleWorkReport execute();

   public abstract MultipleWorkReport execute(String point);

   public abstract void doExecute(String point);

   public abstract MultipleWorkReport executeThen(MultipleWorkReport workReport, String point);

   public abstract void locate2CurrentWork();

   public String getName() {
      return this.name;
   }

   public WorkExecutePolicy getWorkExecutePolicy() {
      return this.workExecutePolicy;
   }

   public WorkContext getWorkContext() {
      return this.workContext;
   }

   public Boolean getBeTrace() {
      return this.beTrace;
   }

   public String getCurrentPoint() {
      return this.currentPoint;
   }

   public List<Function<WorkReport, Work>> getThenFuns() {
      return this.thenFuns;
   }

   public Work getLastWork() {
      return this.lastWork;
   }

   public MultipleWorkReport getExecutedReport() {
      return this.executedReport;
   }

   public Map<String, WorkReport> getExecutedReportMap() {
      return this.executedReportMap;
   }

   public List<Work> getWorkList() {
      return this.workList;
   }

   public Work getPointWork() {
      return this.pointWork;
   }

   public LinkedList<Work> getQueue() {
      return this.queue;
   }

   public MultipleWorkReport getMultipleWorkReport() {
      return this.multipleWorkReport;
   }

   public MultipleWorkReport execute(WorkContext context) {
      this.workContext = context;

      MultipleWorkReport var2;
      try {
         var2 = this.execute();
      } finally {
         this.doLastWork();
      }

      return var2;
   }

   public WorkReport execute(WorkContext context, String point) {
      this.workContext = context;

      MultipleWorkReport var3;
      try {
         var3 = this.execute(point);
      } finally {
         this.doLastWork();
      }

      return var3;
   }

   protected MultipleWorkReport executeInternal(String point) {
      this.multipleWorkReport.setWorkName(this.name);
      this.locate2CurrentWork();
      this.recoveryWorkReport(this.multipleWorkReport);
      this.doExecute(point);
      this.traceReport(this.multipleWorkReport);
      MultipleWorkReport result = this.getPolicyReport(this.multipleWorkReport.getReports(), this.getDefaultWorkContext());
      MultipleWorkReport thenResult = this.executeThen(result, point);
      this.traceReport(thenResult);
      return thenResult;
   }

   protected MultipleWorkReport executeThenInternal(MultipleWorkReport workReport, String point) {
      if (workReport.getStatus() == WorkStatus.STOPPED) {
         return workReport;
      } else {
         List<Work> works = new ArrayList();
         if (Checker.BeNotEmpty(this.thenFuns)) {
            for(Function<WorkReport, Work> fun : this.thenFuns) {
               works.add(wrapNamedPointWork((Work)fun.apply(workReport)));
            }

            this.thenFuns.clear();
         }

         if (Checker.BeEmpty(works)) {
            return workReport;
         } else {
            this.workList.addAll(works);
            if (this.pointWork == null) {
               this.pointWork = (Work)works.get(0);
               return this.execute(point);
            } else {
               return workReport;
            }
         }
      }
   }

   public AbstractWorkFlow lastly(Work work) {
      this.lastWork = work;
      return this;
   }

   protected void doLastWork() {
      if (Checker.BeNotNull(this.lastWork)) {
         WorkReport report = this.doSingleWork(this.lastWork, this.workContext, "");
         this.traceReport(report);
      }

   }

   protected boolean beThePoint(Work work, String point) {
      if (Checker.BeNotEmpty(point) && work instanceof NamedPointWork pointWork) {
         return Checker.BeNotEmpty(pointWork.getPoint()) && pointWork.getPoint().equals(point);
      } else {
         return false;
      }
   }

   protected void locate2CurrentWorkInternal() {
      this.queue = new LinkedList();
      if (Checker.BeNull(this.pointWork)) {
         this.queue.addAll(this.workList);
      } else {
         String currentWorkName = this.getNameOfWork(this.pointWork);
         int index = Iterables.indexOf(this.workList, (w) -> currentWorkName.equals(this.getNameOfWork(w)));
         this.queue.addAll(this.workList.subList(index, this.workList.size()));
         this.pointWork = null;
      }
   }

   private WorkReport doSingleWorkInternal(Work work, WorkContext context, String point) {
      if (Checker.BeNull(context)) {
         if (Checker.BeNotNull(this.workContext)) {
            context = this.workContext;
         } else {
            context = new WorkContext();
         }
      } else if (Checker.BeNotNull(this.workContext)) {
         context.copy(this.workContext);
      }

      WorkReport workReport;
      if (work instanceof PointWorkFlow workFlow) {
         workReport = workFlow.execute(context, point);
      } else {
         Object object = work.execute(context);
         workReport = (new DefaultWorkReport()).setError((Throwable)null).setWorkContext(context).setResult(object).setStatus(WorkStatus.COMPLETED);
         if (work instanceof NamedPointWork pointWork) {
            ((DefaultWorkReport)workReport).setWorkName(((NamedPointWork)work).getName());
            WorkExecuteListener listener = pointWork.getWorkExecuteListener();
            if (Checker.BeNotNull(listener)) {
               listener.onWorkExecute((DefaultWorkReport)workReport, context, (Exception)null);
            }
         }

         if (Checker.BeNotEmpty(point) && this.beThePoint(work, point)) {
            ((DefaultWorkReport)workReport).setStoppedStatus(workReport.getStatus()).setStatus(WorkStatus.STOPPED);
         }
      }

      return workReport;
   }

   protected WorkReport doSingleWork(Work work, WorkContext context, String point) {
      WorkReport workReport;
      try {
         workReport = this.doSingleWorkInternal(work, context, point);
      } catch (Exception e) {
         workReport = (new DefaultWorkReport()).setError(e).setWorkContext(context).setResult((Object)null).setStatus(WorkStatus.FAILED);
         if (work instanceof NamedPointWork pointWork) {
            ((DefaultWorkReport)workReport).setWorkName(((NamedPointWork)work).getName());
            WorkExecuteListener listener = pointWork.getWorkExecuteListener();
            if (Checker.BeNotNull(listener)) {
               listener.onWorkExecute((DefaultWorkReport)workReport, context, e);
            }
         }

         if (Checker.BeNotEmpty(point) && this.beThePoint(work, point)) {
            ((DefaultWorkReport)workReport).setStoppedStatus(workReport.getStatus()).setStatus(WorkStatus.STOPPED);
         }
      }

      return workReport;
   }

   protected boolean beStopped() {
      return this.multipleWorkReport.getStatus() == WorkStatus.STOPPED;
   }

   protected boolean beBreak(WorkReport workReport) {
      if (workReport.getStatus() == WorkStatus.FAILED) {
         if (this.workExecutePolicy == WorkExecutePolicy.FAST_FAIL) {
            return true;
         }

         if (this.workExecutePolicy == WorkExecutePolicy.FAST_FAIL_EXCEPTION) {
            return Checker.BeNotNull(workReport.getError());
         }

         if (this.workExecutePolicy == WorkExecutePolicy.FAST_EXCEPTION) {
            throw new WorkFlowException(workReport.getError());
         }
      } else if (workReport.getStatus() == WorkStatus.COMPLETED) {
         return this.workExecutePolicy == WorkExecutePolicy.FAST_SUCCESS;
      }

      return false;
   }

   protected WorkContext getDefaultWorkContext() {
      WorkContext context = this.workContext;
      if (Checker.BeNull(context)) {
         context = new WorkContext();
      }

      return context;
   }

   protected MultipleWorkReport getPolicyReport(final List<WorkReport> reports, WorkContext context) {
      MultipleWorkReport multipleWorkReport;
      if (WorkExecutePolicy.FAST_SUCCESS == this.workExecutePolicy) {
         multipleWorkReport = this.withFastSuccessResult(reports, context);
      } else if (WorkExecutePolicy.FAST_FAIL == this.workExecutePolicy) {
         multipleWorkReport = this.withFastFailResult(reports, context);
      } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == this.workExecutePolicy) {
         multipleWorkReport = this.withFastFailExceptionResult(reports, context);
      } else if (WorkExecutePolicy.FAST_ALL == this.workExecutePolicy) {
         multipleWorkReport = this.withFastAllResult(reports, context);
      } else if (WorkExecutePolicy.FAST_EXCEPTION == this.workExecutePolicy) {
         multipleWorkReport = this.withFastExceptionallyResult(reports, context);
      } else {
         if (WorkExecutePolicy.FAST_ALL_SUCCESS != this.workExecutePolicy) {
            throw new RuntimeException("Not support work execute policy:" + String.valueOf(this.workExecutePolicy));
         }

         multipleWorkReport = this.withFastAllSuccessResult(reports, context);
      }

      multipleWorkReport.setWorkName(this.name);
      return multipleWorkReport;
   }

   protected MultipleWorkReport withFastFailResult(List<WorkReport> reports, WorkContext workContext) {
      MultipleWorkReport workReport = new MultipleWorkReport();
      if (Checker.BeEmpty(reports)) {
         workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
         return workReport;
      } else {
         WorkReport report = (WorkReport)reports.stream().filter((r) -> WorkStatus.FAILED.equals(r.getStatus())).findFirst().orElse((Object)null);
         if (report != null) {
            workReport.addReport(report);
            workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         } else {
            workReport.addAllReports(reports);
            workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
         }

         return workReport;
      }
   }

   protected MultipleWorkReport withFastFailExceptionResult(List<WorkReport> reports, WorkContext workContext) {
      MultipleWorkReport workReport = new MultipleWorkReport();
      if (Checker.BeEmpty(reports)) {
         workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
         return workReport;
      } else {
         WorkReport report = (WorkReport)reports.stream().filter((r) -> WorkStatus.FAILED.equals(r.getStatus()) && Checker.BeNotNull(r.getError())).findFirst().orElse((Object)null);
         if (report != null) {
            workReport.addReport(report);
            workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         } else {
            workReport.addAllReports(reports);
            workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
         }

         return workReport;
      }
   }

   protected MultipleWorkReport withFastSuccessResult(List<WorkReport> reports, WorkContext workContext) {
      MultipleWorkReport workReport = new MultipleWorkReport();
      if (Checker.BeEmpty(reports)) {
         workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         return workReport;
      } else {
         WorkReport report = (WorkReport)reports.stream().filter((r) -> WorkStatus.COMPLETED.equals(r.getStatus())).findFirst().orElse((Object)null);
         if (report != null) {
            workReport.addReport(report);
            workReport.setWorkContext(this.workContext).setStatus(WorkStatus.COMPLETED);
         } else {
            workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         }

         return workReport;
      }
   }

   protected MultipleWorkReport withFastAllSuccessResult(List<WorkReport> reports, WorkContext workContext) {
      MultipleWorkReport workReport = new MultipleWorkReport();
      if (Checker.BeEmpty(reports)) {
         workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         return workReport;
      } else {
         List<WorkReport> successReports = (List)reports.stream().filter((r) -> WorkStatus.COMPLETED.equals(r.getStatus())).collect(Collectors.toList());
         if (Checker.BeNotEmpty(successReports)) {
            workReport.addAllReports(successReports);
            workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
         } else {
            workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         }

         return workReport;
      }
   }

   protected MultipleWorkReport withFastExceptionallyResult(List<WorkReport> reports, WorkContext workContext) {
      MultipleWorkReport workReport = new MultipleWorkReport();
      if (Checker.BeEmpty(reports)) {
         workReport.setStatus(WorkStatus.FAILED).setWorkContext(workContext);
         return workReport;
      } else {
         WorkReport report = (WorkReport)reports.stream().filter((r) -> Checker.BeNotNull(r.getError())).findFirst().orElse((Object)null);
         if (Checker.BeNotNull(report)) {
            Throwable throwable = report.getError();
            if (throwable instanceof WorkFlowException) {
               throw (WorkFlowException)throwable;
            } else if (throwable instanceof RuntimeException) {
               throw (RuntimeException)throwable;
            } else {
               throw new WorkFlowException(workReport.getError());
            }
         } else {
            workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
            return workReport;
         }
      }
   }

   protected MultipleWorkReport withFastAllResult(List<WorkReport> reports, WorkContext workContext) {
      this.assertReportNotEmpty(reports);
      MultipleWorkReport workReport = new MultipleWorkReport();
      workReport.addAllReports(reports);
      workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
      return workReport;
   }

   private void assertReportNotEmpty(List<WorkReport> reports) {
      if (Checker.BeEmpty(reports)) {
         throw new WorkFlowException("Work reports is empty!");
      }
   }

   private void mappedReport(WorkReport report) {
      if (report instanceof MultipleWorkReport multipleWorkReport) {
         this.executedReportMap.put(multipleWorkReport.getWorkName(), report);
         this.mappedReports(((MultipleWorkReport)report).getReports());
      } else if (report instanceof DefaultWorkReport) {
         this.executedReportMap.put(report.getWorkName(), report);
      }

   }

   private void mappedReports(List<WorkReport> reports) {
      for(WorkReport report : reports) {
         this.mappedReport(report);
      }

   }

   protected void traceReport(WorkReport report) {
      if (this.beTrace) {
         this.executedReport.addReport(report);
         this.mappedReport(report);
      }

   }

   protected static Work wrapNamedPointWork(Work work) {
      if (work instanceof NamedPointWork) {
         return work;
      } else {
         NamedPointWork namedPointWork = new NamedPointWork(work);
         return (Work)(!(work instanceof WorkFlow) ? namedPointWork : work);
      }
   }

   protected String getNameOfWork(Work work) {
      String name = "";
      if (work instanceof NamedWork namedWork) {
         name = namedWork.getName();
      } else if (work instanceof AbstractWorkFlow abstractWorkFlow) {
         name = abstractWorkFlow.getName();
      }

      return name;
   }

   protected void recoveryWorkReport(WorkReport report) {
      if (report instanceof DefaultWorkReport defaultWorkReport) {
         if (defaultWorkReport.getStatus() == WorkStatus.STOPPED) {
            defaultWorkReport.setStatus(defaultWorkReport.getStoppedStatus()).setStoppedStatus((WorkStatus)null);
         }

      } else {
         MultipleWorkReport multipleWorkReport = (MultipleWorkReport)report;
         List<WorkReport> workReports = multipleWorkReport.getReports();
         multipleWorkReport.setStatus((WorkStatus)null);
         if (Checker.BeNotEmpty(workReports)) {
            if (report instanceof ParallelWorkReport) {
               for(WorkReport workReport : workReports) {
                  this.recoveryWorkReport(workReport);
               }
            } else {
               WorkReport workReport = (WorkReport)workReports.get(workReports.size() - 1);
               this.recoveryWorkReport(workReport);
            }
         }

      }
   }
}
