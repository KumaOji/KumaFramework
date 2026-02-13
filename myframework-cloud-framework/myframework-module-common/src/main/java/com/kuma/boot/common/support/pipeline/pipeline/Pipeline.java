/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline.pipeline;

import com.kuma.boot.common.support.pipeline.pipeline.PipelineBuilder;
import com.kuma.boot.common.support.pipeline.pipeline.PipelineContext;

public interface Pipeline<T> {
    public PipelineContext<T> execute(T var1);

    public static <T> PipelineBuilder<T> builder() {
        return new PipelineBuilder();
    }
}

