/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline.pipeline;

import com.kuma.boot.common.support.pipeline.pipeline.FailureStrategy;
import com.kuma.boot.common.support.pipeline.pipeline.PipelineContext;
import com.kuma.boot.common.support.pipeline.pipeline.PipelineException;

public interface PipelineNode<T> {
    public void execute(PipelineContext<T> var1) throws PipelineException;

    default public String getName() {
        return this.getClass().getSimpleName();
    }

    default public FailureStrategy getFailureStrategy() {
        return FailureStrategy.STOP;
    }
}

