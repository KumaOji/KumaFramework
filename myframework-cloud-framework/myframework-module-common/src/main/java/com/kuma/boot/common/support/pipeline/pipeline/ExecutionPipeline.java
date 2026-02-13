/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline.pipeline;

import com.kuma.boot.common.support.pipeline.pipeline.FailureStrategy;
import com.kuma.boot.common.support.pipeline.pipeline.Pipeline;
import com.kuma.boot.common.support.pipeline.pipeline.PipelineContext;
import com.kuma.boot.common.support.pipeline.pipeline.PipelineNode;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.List;

public class ExecutionPipeline<T>
implements Pipeline<T> {
    private final List<PipelineNode<T>> nodes;
    private final String name;

    ExecutionPipeline(List<PipelineNode<T>> nodes, String name) {
        this.nodes = nodes;
        this.name = name;
    }

    @Override
    public PipelineContext<T> execute(T data) {
        LogUtils.info("Pipeline [{}] started with {} nodes", this.name, this.nodes.size());
        PipelineContext<T> context = new PipelineContext<T>(data);
        for (PipelineNode<T> node : this.nodes) {
            if (context.isInterrupted()) {
                LogUtils.info("Pipeline [{}] interrupted: {}", this.name, context.getInterruptReason());
                break;
            }
            this.executeNode(node, context);
        }
        LogUtils.info("Pipeline [{}] completed. Executed: {}, Failures: {}", this.name, context.getExecutedNodes().size(), context.getFailures().size());
        return context;
    }

    private void executeNode(PipelineNode<T> node, PipelineContext<T> context) {
        String nodeName = node.getName();
        LogUtils.debug("Executing node: {}", nodeName);
        try {
            node.execute(context);
            context.markNodeExecuted(nodeName);
            LogUtils.debug("Node [{}] executed successfully", nodeName);
        }
        catch (Exception e) {
            LogUtils.error("Node [{}] execution failed", nodeName, e);
            FailureStrategy strategy = node.getFailureStrategy();
            context.recordFailure(nodeName, e.getMessage(), e);
            switch (strategy) {
                case STOP: {
                    context.interrupt("Node [" + nodeName + "] failed with STOP strategy");
                    break;
                }
            }
        }
    }
}

