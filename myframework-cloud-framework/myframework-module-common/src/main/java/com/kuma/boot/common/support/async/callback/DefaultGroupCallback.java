/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.async.callback;

import com.kuma.boot.common.support.async.callback.IGroupCallback;
import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.List;

public class DefaultGroupCallback
implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {
    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {
    }
}

