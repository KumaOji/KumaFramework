/**
 * Copyright (c) 2025-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kuma.boot.flowengine.easywork.flow;

import com.kuma.boot.flowengine.easywork.context.WorkContext;
import com.kuma.boot.flowengine.easywork.exception.WorkFlowException;
import com.kuma.boot.flowengine.easywork.report.MultipleWorkReport;
import com.kuma.boot.flowengine.easywork.report.ParallelWorkReport;
import com.kuma.boot.flowengine.easywork.report.WorkReport;
import com.kuma.boot.flowengine.easywork.util.Checker;
import com.kuma.boot.flowengine.easywork.util.Strings;
import com.kuma.boot.flowengine.easywork.work.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.kuma.boot.flowengine.easywork.report.ParallelWorkReport.aNewParallelWorkReport;
import static com.kuma.boot.flowengine.easywork.work.NamedParallelWork.aNewParallelWork;
import static com.kuma.boot.flowengine.easywork.work.WorkStatus.FAILED;

/**
 * A parallel flow executes a set of work units in parallel. A {@link ParallelFlow}
 * requires a {@link ExecutorService} to execute work units in parallel using multiple
 * threads.
 *
 * @author zening (316279829@qq.com)
 */
public class ParallelFlow extends AbstractWorkFlow {

    private static ExecutorService executor = null;
    // shutdown thread pool when setting true
    private boolean autoShutdown = false;
    // join timeout
    private int timeoutInSeconds = 60;

    private ParallelFlow(List<Work> works) {
        List<Work> theWorks = new ArrayList<>();

        for (Work work : works) {
            theWorks.add(wrapNamedPointWork(work));
        }

        workList.add(aNewParallelWork(theWorks));
        workList.add(new EndWork());

        if (executor == null) {
            executor = initExecutor();
        }
    }

    @Override
    public ParallelWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    @Override
    public ParallelWorkReport execute(String point) {
        try {
            return aNewParallelWorkReport(executeInternal(point));
        } finally {
            shutdown();
        }
    }

    @Override
    public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
        return executeThenInternal(workReport, point);
    }

    @Override
    public void doExecute(String point) {
        if (beStopped()) {
            return;
        }

        WorkContext workContext = getDefaultWorkContext();
        Work work = queue.poll();
        if (work instanceof EndWork) {
            return;
        }

        WorkReport report;

        if (work instanceof NamedParallelWork) {
            report = doParallelWork((NamedParallelWork) work);
        } else {
            report = doSingleWork(work, workContext, point);
            multipleWorkReport.addReport(report);
        }

        if (beStopped()) {
            if (work instanceof WorkFlow) {
                queue.offerFirst(work);
            }
            pointWork = queue.peek();
            return;
        }

        if (beBreak(report)) {
            return;
        }

        if (report.getStatus() != WorkStatus.STOPPED) {
            doExecute(point);
        }
    }

    @Override
    public void locate2CurrentWork() {
        locate2CurrentWorkInternal();
    }

    private MultipleWorkReport doParallelWork(NamedParallelWork wrapper) {
        WorkContext context = getDefaultWorkContext();

        List<Work> works = wrapper.getSupplierWorks();
        List<Supplier<WorkReport>> supplierList = new ArrayList<>();
        works.forEach(work -> supplierList.add(() -> doSingleWork(work, context, Strings.EMPTY)));

        if (WorkExecutePolicy.FAST_SUCCESS == workExecutePolicy) {
            CompletableFuture<WorkReport> report = supplyAnySuccessAsync(supplierList);
            multipleWorkReport = withFastSuccessResult(report, context);
        } else if (WorkExecutePolicy.FAST_FAIL == workExecutePolicy) {
            CompletableFuture<List<WorkReport>> reports = supplyAllAsync(supplierList);
            multipleWorkReport = withFastFailResult(reports, context);
        } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == workExecutePolicy) {
            CompletableFuture<List<WorkReport>> reports = supplyAllAsync(supplierList);
            multipleWorkReport = withFastFailExceptionResult(reports, context);
        } else if (WorkExecutePolicy.FAST_ALL == workExecutePolicy) {
            CompletableFuture<List<WorkReport>> reports = supplyAllAsync(supplierList);
            multipleWorkReport = withFastAllResult(reports, context);
        } else if (WorkExecutePolicy.FAST_EXCEPTION == workExecutePolicy) {
            CompletableFuture<List<WorkReport>> reports = supplyFailFastAsync(supplierList);
            multipleWorkReport = withFastExceptionallyResult(reports, context);
        } else if (WorkExecutePolicy.FAST_ALL_SUCCESS == workExecutePolicy) {
            CompletableFuture<List<WorkReport>> reports = supplyAllAsync(supplierList);
            multipleWorkReport = withFastAllSuccessResult(reports, context);
        } else {
            throw new RuntimeException("Not support work execute policy:" + workExecutePolicy);
        }
        multipleWorkReport.setWorkName(name);
        return multipleWorkReport;
    }

    // ---- parallel execution helpers ----------------------------------------

    /** Completes as soon as any supplier returns a result. */
    private CompletableFuture<WorkReport> supplyAnySuccessAsync(List<Supplier<WorkReport>> suppliers) {
        CompletableFuture<WorkReport> result = new CompletableFuture<>();
        List<CompletableFuture<WorkReport>> futures = suppliers.stream()
                .map(s -> CompletableFuture.supplyAsync(s, executor))
                .collect(Collectors.toList());
        futures.forEach(f -> f.thenAccept(r -> result.complete(r)));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((v, ex) -> {
                    if (!result.isDone()) {
                        result.completeExceptionally(
                                ex != null ? ex : new RuntimeException("All parallel tasks failed"));
                    }
                });
        return result;
    }

    /** Runs all suppliers in parallel and collects all results. */
    private CompletableFuture<List<WorkReport>> supplyAllAsync(List<Supplier<WorkReport>> suppliers) {
        List<CompletableFuture<WorkReport>> futures = suppliers.stream()
                .map(s -> CompletableFuture.supplyAsync(s, executor))
                .collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /** Runs all suppliers in parallel; completes exceptionally on the first exception. */
    private CompletableFuture<List<WorkReport>> supplyFailFastAsync(List<Supplier<WorkReport>> suppliers) {
        List<CompletableFuture<WorkReport>> futures = suppliers.stream()
                .map(s -> CompletableFuture.supplyAsync(s, executor))
                .collect(Collectors.toList());
        CompletableFuture<List<WorkReport>> failFast = new CompletableFuture<>();
        futures.forEach(f -> f.exceptionally(ex -> {
            failFast.completeExceptionally(ex);
            return null;
        }));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .thenAccept(list -> { if (!failFast.isDone()) failFast.complete(list); });
        return failFast;
    }

    // ---- result-aggregation helpers ----------------------------------------

    private ParallelWorkReport withFastFailResult(CompletableFuture<List<WorkReport>> future, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastFailResult(reports, workContext));
        } catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastFailExceptionResult(CompletableFuture<List<WorkReport>> future, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastFailExceptionResult(reports, workContext));
        } catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastSuccessResult(CompletableFuture<WorkReport> future, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            WorkReport report = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastSuccessResult(Lists.newArrayList(report), workContext));
        } catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastAllResult(CompletableFuture<List<WorkReport>> future, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastAllResult(reports, workContext));
        } catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastAllSuccessResult(CompletableFuture<List<WorkReport>> future, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastAllResult(reports, workContext));
        } catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastExceptionallyResult(CompletableFuture<List<WorkReport>> future, WorkContext workContext) {
        try {
            List<WorkReport> reports = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            return aNewParallelWorkReport(withFastExceptionallyResult(reports, workContext));
        } catch (Exception e) {
            if (e instanceof WorkFlowException) {
                throw (WorkFlowException) e;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    // ---- builder / config --------------------------------------------------

    public ParallelFlow named(String name) {
        this.name = name;
        return this;
    }

    public ParallelFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public ParallelFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public ParallelFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }

    @Override
    public ParallelFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public ParallelFlow then(Work work) {
        thenFuns.add(report -> work);
        return this;
    }

    public ParallelFlow addWork(Work work) {
        NamedParallelWork parallelWork = (NamedParallelWork) Iterables.find(workList, w -> w instanceof NamedParallelWork);
        if (parallelWork != null) {
            parallelWork.addWork(work);
        }
        return this;
    }

    private ThreadPoolExecutor initExecutor() {
        final int corePoolSize = 10;
        final int maxPoolSize = 20;
        final int queueCapacity = 50;
        final int keepAliveTime = 30;
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity, true);
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                queue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ParallelFlow aNewParallelFlow(Work... works) {
        return new ParallelFlow(Arrays.asList(works));
    }

    public ParallelFlow withExecutor(ExecutorService theExecutor) {
        if (Checker.BeNotNull(theExecutor)) {
            executor = theExecutor;
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
        if (autoShutdown && executor != null) {
            executor.shutdown();
        }
    }
}
