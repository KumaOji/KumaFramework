/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.wrapper;

import com.kuma.boot.common.support.async.callback.DefaultCallback;
import com.kuma.boot.common.support.async.callback.ICallback;
import com.kuma.boot.common.support.async.callback.IWorker;
import com.kuma.boot.common.support.async.exception.SkippedException;
import com.kuma.boot.common.support.async.executor.timer.SystemClock;
import com.kuma.boot.common.support.async.worker.DependWrapper;
import com.kuma.boot.common.support.async.worker.ResultState;
import com.kuma.boot.common.support.async.worker.WorkResult;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerWrapper<T, V> {
    private String id;
    private T param;
    private IWorker<T, V> worker;
    private ICallback<T, V> callback;
    private List<WorkerWrapper<?, ?>> nextWrappers;
    private List<DependWrapper> dependWrappers;
    private AtomicInteger state = new AtomicInteger(0);
    private Map<String, WorkerWrapper<?, ?>> forParamUseWrappers;
    private volatile WorkResult<V> workResult = WorkResult.defaultResult();
    private volatile boolean needCheckNextWrapperResult = true;
    private static final int FINISH = 1;
    private static final int ERROR = 2;
    private static final int WORKING = 3;
    private static final int INIT = 0;

    private WorkerWrapper(String id, IWorker<T, V> worker, T param, ICallback<T, V> callback) {
        if (worker == null) {
            throw new NullPointerException("async.worker is null");
        }
        this.worker = worker;
        this.param = param;
        this.id = id;
        if (callback == null) {
            callback = new DefaultCallback();
        }
        this.callback = callback;
    }

    private void work(ExecutorService executorService, WorkerWrapper<?, ?> fromWrapper, long remainTime, Map<String, WorkerWrapper<?, ?>> forParamUseWrappers) {
        this.forParamUseWrappers = forParamUseWrappers;
        forParamUseWrappers.put(this.id, this);
        long now = SystemClock.now();
        if (remainTime <= 0L) {
            this.fastFail(0, null);
            this.beginNext(executorService, now, remainTime);
            return;
        }
        if (this.getState() == 1 || this.getState() == 2) {
            this.beginNext(executorService, now, remainTime);
            return;
        }
        if (this.needCheckNextWrapperResult && !this.checkNextWrapperResult()) {
            this.fastFail(0, new SkippedException());
            this.beginNext(executorService, now, remainTime);
            return;
        }
        if (this.dependWrappers == null || this.dependWrappers.isEmpty()) {
            this.fire();
            this.beginNext(executorService, now, remainTime);
            return;
        }
        if (this.dependWrappers.size() == 1) {
            this.doDependsOneJob(fromWrapper);
            this.beginNext(executorService, now, remainTime);
        } else {
            this.doDependsJobs(executorService, this.dependWrappers, fromWrapper, now, remainTime);
        }
    }

    public void work(ExecutorService executorService, long remainTime, Map<String, WorkerWrapper<?, ?>> forParamUseWrappers) {
        this.work(executorService, null, remainTime, forParamUseWrappers);
    }

    public void stopNow() {
        if (this.getState() == 0 || this.getState() == 3) {
            this.fastFail(this.getState(), null);
        }
    }

    private boolean checkNextWrapperResult() {
        if (this.nextWrappers == null || this.nextWrappers.size() != 1) {
            return this.getState() == 0;
        }
        WorkerWrapper<?, ?> nextWrapper = this.nextWrappers.get(0);
        boolean state = nextWrapper.getState() == 0;
        return state && nextWrapper.checkNextWrapperResult();
    }

    private void beginNext(ExecutorService executorService, long now, long remainTime) {
        long costTime = SystemClock.now() - now;
        if (this.nextWrappers == null) {
            return;
        }
        if (this.nextWrappers.size() == 1) {
            this.nextWrappers.get(0).work(executorService, this, remainTime - costTime, this.forParamUseWrappers);
            return;
        }
        CompletableFuture[] futures = new CompletableFuture[this.nextWrappers.size()];
        for (int i = 0; i < this.nextWrappers.size(); ++i) {
            int finalI = i;
            futures[i] = CompletableFuture.runAsync(() -> this.nextWrappers.get(finalI).work(executorService, this, remainTime - costTime, this.forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(remainTime - costTime, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            LogUtils.error(e);
        }
    }

    private void doDependsOneJob(WorkerWrapper<?, ?> dependWrapper) {
        if (ResultState.TIMEOUT == dependWrapper.getWorkResult().getResultState()) {
            this.workResult = this.defaultResult();
            this.fastFail(0, null);
        } else if (ResultState.EXCEPTION == dependWrapper.getWorkResult().getResultState()) {
            this.workResult = this.defaultExResult(dependWrapper.getWorkResult().getEx());
            this.fastFail(0, null);
        } else {
            this.fire();
        }
    }

    private synchronized void doDependsJobs(ExecutorService executorService, List<DependWrapper> dependWrappers, WorkerWrapper<?, ?> fromWrapper, long now, long remainTime) {
        if (this.getState() != 0) {
            return;
        }
        boolean nowDependIsMust = false;
        HashSet<DependWrapper> mustWrapper = new HashSet<DependWrapper>();
        for (DependWrapper dependWrapper : dependWrappers) {
            if (dependWrapper.isMust()) {
                mustWrapper.add(dependWrapper);
            }
            if (!dependWrapper.getDependWrapper().equals(fromWrapper)) continue;
            nowDependIsMust = dependWrapper.isMust();
        }
        if (mustWrapper.isEmpty()) {
            if (ResultState.TIMEOUT == fromWrapper.getWorkResult().getResultState()) {
                this.fastFail(0, null);
            } else {
                this.fire();
            }
            this.beginNext(executorService, now, remainTime);
            return;
        }
        if (!nowDependIsMust) {
            return;
        }
        boolean existNoFinish = false;
        boolean hasError = false;
        for (DependWrapper dependWrapper : mustWrapper) {
            WorkerWrapper<?, ?> workerWrapper = dependWrapper.getDependWrapper();
            WorkResult<?> tempWorkResult = workerWrapper.getWorkResult();
            if (workerWrapper.getState() == 0 || workerWrapper.getState() == 3) {
                existNoFinish = true;
                break;
            }
            if (ResultState.TIMEOUT == tempWorkResult.getResultState()) {
                this.workResult = this.defaultResult();
                hasError = true;
                break;
            }
            if (ResultState.EXCEPTION != tempWorkResult.getResultState()) continue;
            this.workResult = this.defaultExResult(workerWrapper.getWorkResult().getEx());
            hasError = true;
            break;
        }
        if (hasError) {
            this.fastFail(0, null);
            this.beginNext(executorService, now, remainTime);
            return;
        }
        if (!existNoFinish) {
            this.fire();
            this.beginNext(executorService, now, remainTime);
            return;
        }
    }

    private void fire() {
        this.workResult = this.workerDoJob();
    }

    private boolean fastFail(int expect, Exception e) {
        if (!this.compareAndSetState(expect, 2)) {
            return false;
        }
        if (this.checkIsNullResult()) {
            this.workResult = e == null ? this.defaultResult() : this.defaultExResult(e);
        }
        this.callback.result(false, this.param, this.workResult);
        return true;
    }

    private WorkResult<V> workerDoJob() {
        if (!this.checkIsNullResult()) {
            return this.workResult;
        }
        try {
            if (!this.compareAndSetState(0, 3)) {
                return this.workResult;
            }
            this.callback.begin();
            V resultValue = this.worker.action(this.param, this.forParamUseWrappers);
            if (!this.compareAndSetState(3, 1)) {
                return this.workResult;
            }
            this.workResult.setResultState(ResultState.SUCCESS);
            this.workResult.setResult(resultValue);
            this.callback.result(true, this.param, this.workResult);
            return this.workResult;
        }
        catch (Exception e) {
            if (!this.checkIsNullResult()) {
                return this.workResult;
            }
            this.fastFail(3, e);
            return this.workResult;
        }
    }

    public WorkResult<V> getWorkResult() {
        return this.workResult;
    }

    public List<WorkerWrapper<?, ?>> getNextWrappers() {
        return this.nextWrappers;
    }

    public void setParam(T param) {
        this.param = param;
    }

    private boolean checkIsNullResult() {
        return ResultState.DEFAULT == this.workResult.getResultState();
    }

    private void addDepend(WorkerWrapper<?, ?> workerWrapper, boolean must) {
        this.addDepend(new DependWrapper(workerWrapper, must));
    }

    private void addDepend(DependWrapper dependWrapper) {
        if (this.dependWrappers == null) {
            this.dependWrappers = new ArrayList<DependWrapper>();
        }
        for (DependWrapper wrapper : this.dependWrappers) {
            if (!wrapper.equals(dependWrapper)) continue;
            return;
        }
        this.dependWrappers.add(dependWrapper);
    }

    private void addNext(WorkerWrapper<?, ?> workerWrapper) {
        if (this.nextWrappers == null) {
            this.nextWrappers = new ArrayList();
        }
        for (WorkerWrapper<?, ?> wrapper : this.nextWrappers) {
            if (!workerWrapper.equals(wrapper)) continue;
            return;
        }
        this.nextWrappers.add(workerWrapper);
    }

    private void addNextWrappers(List<WorkerWrapper<?, ?>> wrappers) {
        if (wrappers == null) {
            return;
        }
        for (WorkerWrapper<?, ?> wrapper : wrappers) {
            this.addNext(wrapper);
        }
    }

    private void addDependWrappers(List<DependWrapper> dependWrappers) {
        if (dependWrappers == null) {
            return;
        }
        for (DependWrapper wrapper : dependWrappers) {
            this.addDepend(wrapper);
        }
    }

    private WorkResult<V> defaultResult() {
        this.workResult.setResultState(ResultState.TIMEOUT);
        this.workResult.setResult(this.worker.defaultValue());
        return this.workResult;
    }

    private WorkResult<V> defaultExResult(Exception ex) {
        this.workResult.setResultState(ResultState.EXCEPTION);
        this.workResult.setResult(this.worker.defaultValue());
        this.workResult.setEx(ex);
        return this.workResult;
    }

    private int getState() {
        return this.state.get();
    }

    public String getId() {
        return this.id;
    }

    private boolean compareAndSetState(int expect, int update) {
        return this.state.compareAndSet(expect, update);
    }

    private void setNeedCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
        this.needCheckNextWrapperResult = needCheckNextWrapperResult;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WorkerWrapper that = (WorkerWrapper)o;
        return this.needCheckNextWrapperResult == that.needCheckNextWrapperResult && Objects.equals(this.param, that.param) && Objects.equals(this.worker, that.worker) && Objects.equals(this.callback, that.callback) && Objects.equals(this.nextWrappers, that.nextWrappers) && Objects.equals(this.dependWrappers, that.dependWrappers) && Objects.equals(this.state, that.state) && Objects.equals(this.workResult, that.workResult);
    }

    public int hashCode() {
        return Objects.hash(this.param, this.worker, this.callback, this.nextWrappers, this.dependWrappers, this.state, this.workResult, this.needCheckNextWrapperResult);
    }

    public static class Builder<W, C> {
        private String id = UUID.randomUUID().toString();
        private W param;
        private IWorker<W, C> worker;
        private ICallback<W, C> callback;
        private List<WorkerWrapper<?, ?>> nextWrappers;
        private List<DependWrapper> dependWrappers;
        private Set<WorkerWrapper<?, ?>> selfIsMustSet;
        private boolean needCheckNextWrapperResult = true;

        public Builder<W, C> worker(IWorker<W, C> worker) {
            this.worker = worker;
            return this;
        }

        public Builder<W, C> param(W w) {
            this.param = w;
            return this;
        }

        public Builder<W, C> id(String id) {
            if (id != null) {
                this.id = id;
            }
            return this;
        }

        public Builder<W, C> needCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
            this.needCheckNextWrapperResult = needCheckNextWrapperResult;
            return this;
        }

        public Builder<W, C> callback(ICallback<W, C> callback) {
            this.callback = callback;
            return this;
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> ... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                this.depend(wrapper);
            }
            return this;
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper) {
            return this.depend(wrapper, true);
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper, boolean isMust) {
            if (wrapper == null) {
                return this;
            }
            DependWrapper dependWrapper = new DependWrapper(wrapper, isMust);
            if (this.dependWrappers == null) {
                this.dependWrappers = new ArrayList<DependWrapper>();
            }
            this.dependWrappers.add(dependWrapper);
            return this;
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> wrapper) {
            return this.next(wrapper, true);
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> wrapper, boolean selfIsMust) {
            if (this.nextWrappers == null) {
                this.nextWrappers = new ArrayList();
            }
            this.nextWrappers.add(wrapper);
            if (selfIsMust) {
                if (this.selfIsMustSet == null) {
                    this.selfIsMustSet = new HashSet();
                }
                this.selfIsMustSet.add(wrapper);
            }
            return this;
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> ... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                this.next(wrapper);
            }
            return this;
        }

        public WorkerWrapper<W, C> build() {
            WorkerWrapper<W, C> wrapper = new WorkerWrapper<W, C>(this.id, this.worker, this.param, this.callback);
            wrapper.setNeedCheckNextWrapperResult(this.needCheckNextWrapperResult);
            if (this.dependWrappers != null) {
                for (DependWrapper dependWrapper : this.dependWrappers) {
                    dependWrapper.getDependWrapper().addNext(wrapper);
                    wrapper.addDepend(dependWrapper);
                }
            }
            if (this.nextWrappers != null) {
                for (WorkerWrapper workerWrapper : this.nextWrappers) {
                    boolean must = this.selfIsMustSet != null && this.selfIsMustSet.contains(workerWrapper);
                    workerWrapper.addDepend(wrapper, must);
                    wrapper.addNext(workerWrapper);
                }
            }
            return wrapper;
        }
    }
}

