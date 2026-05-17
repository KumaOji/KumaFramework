package com.kuma.boot.common.support.pipeline.pipeline;


import com.kuma.boot.common.utils.log.LogUtils;

import java.util.List;

/**
 * 执行管道实现
 *
 * @param <T> 数据类型
 */
public class ExecutionPipeline<T> implements Pipeline<T> {

    private final List<PipelineNode<T>> nodes;
    private final String name;

    ExecutionPipeline(List<PipelineNode<T>> nodes, String name) {
        this.nodes = nodes;
        this.name = name;
    }

    @Override
    public PipelineContext<T> execute(T data) {
        LogUtils.info("Pipeline [{}] started with {} nodes", name, nodes.size());

        PipelineContext<T> context = new PipelineContext<>(data);

        for (PipelineNode<T> node : nodes) {
            if (context.isInterrupted()) {
                LogUtils.info("Pipeline [{}] interrupted: {}", name, context.getInterruptReason());
                break;
            }

            executeNode(node, context);
        }

        LogUtils.info("Pipeline [{}] completed. Executed: {}, Failures: {}",
                name, context.getExecutedNodes().size(), context.getFailures().size());

        return context;
    }

    private void executeNode(PipelineNode<T> node, PipelineContext<T> context) {
        String nodeName = node.getName();
        LogUtils.debug("Executing node: {}", nodeName);

        try {
            node.execute(context);
            context.markNodeExecuted(nodeName);
            LogUtils.debug("Node [{}] executed successfully", nodeName);
        } catch (Exception e) {
            LogUtils.error("Node [{}] execution failed", nodeName, e);

            FailureStrategy strategy = node.getFailureStrategy();
            context.recordFailure(nodeName, e.getMessage(), e);

            switch (strategy) {
                case STOP:
                    context.interrupt("Node [" + nodeName + "] failed with STOP strategy");
                    break;
                case CONTINUE:
                case SKIP:
                    // 继续执行下一个节点
                    break;
            }
        }
    }
}
