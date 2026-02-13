/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline.pipeline;

import com.kuma.boot.common.support.pipeline.pipeline.ExecutionPipeline;
import com.kuma.boot.common.support.pipeline.pipeline.Pipeline;
import com.kuma.boot.common.support.pipeline.pipeline.PipelineNode;
import java.util.ArrayList;
import java.util.List;

public class PipelineBuilder<T> {
    private final List<PipelineNode<T>> nodes = new ArrayList<PipelineNode<T>>();
    private String name = "DefaultPipeline";

    public PipelineBuilder<T> add(PipelineNode<T> node) {
        this.nodes.add(node);
        return this;
    }

    public PipelineBuilder<T> addAll(List<PipelineNode<T>> newNodes) {
        this.nodes.addAll(newNodes);
        return this;
    }

    public PipelineBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public PipelineBuilder<T> addIf(boolean condition, PipelineNode<T> node) {
        if (condition) {
            this.nodes.add(node);
        }
        return this;
    }

    public Pipeline<T> build() {
        if (this.nodes.isEmpty()) {
            throw new IllegalStateException("Pipeline must have at least one node");
        }
        return new ExecutionPipeline<T>(this.nodes, this.name);
    }
}

