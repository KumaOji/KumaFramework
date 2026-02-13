/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.worker;

import com.kuma.boot.common.support.async.worker.ResultState;

public class WorkResult<V> {
    private V result;
    private ResultState resultState;
    private Exception ex;

    public WorkResult(V result, ResultState resultState) {
        this(result, resultState, null);
    }

    public WorkResult(V result, ResultState resultState, Exception ex) {
        this.result = result;
        this.resultState = resultState;
        this.ex = ex;
    }

    public static <V> WorkResult<V> defaultResult() {
        return new WorkResult<Object>(null, ResultState.DEFAULT);
    }

    public String toString() {
        return "WorkResult{result=" + String.valueOf(this.result) + ", resultState=" + String.valueOf((Object)this.resultState) + ", ex=" + String.valueOf(this.ex) + "}";
    }

    public Exception getEx() {
        return this.ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public V getResult() {
        return this.result;
    }

    public void setResult(V result) {
        this.result = result;
    }

    public ResultState getResultState() {
        return this.resultState;
    }

    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }
}

