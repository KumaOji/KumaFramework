/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.callback;

import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.List;

public interface IGroupCallback {
    public void success(List<WorkerWrapper> var1);

    public void failure(List<WorkerWrapper> var1, Exception var2);
}

