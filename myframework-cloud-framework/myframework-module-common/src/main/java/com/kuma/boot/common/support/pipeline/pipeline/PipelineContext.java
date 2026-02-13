/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineContext<T> {
    private final T data;
    private boolean interrupted;
    private String interruptReason;
    private final List<String> executedNodes;
    private final List<NodeFailure> failures;
    private final Map<String, Object> attributes;

    public PipelineContext(T data) {
        this.data = data;
        this.executedNodes = new ArrayList<String>();
        this.failures = new ArrayList<NodeFailure>();
        this.attributes = new HashMap<String, Object>();
        this.interrupted = false;
    }

    public void interrupt(String reason) {
        this.interrupted = true;
        this.interruptReason = reason;
    }

    public void markNodeExecuted(String nodeName) {
        this.executedNodes.add(nodeName);
    }

    public void recordFailure(String nodeName, String reason, Throwable cause) {
        this.failures.add(new NodeFailure(nodeName, reason, cause));
    }

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public <V> V getAttribute(String key) {
        return (V)this.attributes.get(key);
    }

    public <V> V getAttribute(String key, V defaultValue) {
        return (V)this.attributes.getOrDefault(key, defaultValue);
    }

    public T getData() {
        return this.data;
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }

    public String getInterruptReason() {
        return this.interruptReason;
    }

    public List<String> getExecutedNodes() {
        return this.executedNodes;
    }

    public List<NodeFailure> getFailures() {
        return this.failures;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public static class NodeFailure {
        private final String nodeName;
        private final String reason;
        private final Throwable cause;
        private final long timestamp;

        public NodeFailure(String nodeName, String reason, Throwable cause) {
            this.nodeName = nodeName;
            this.reason = reason;
            this.cause = cause;
            this.timestamp = System.currentTimeMillis();
        }

        public String getNodeName() {
            return this.nodeName;
        }

        public String getReason() {
            return this.reason;
        }

        public Throwable getCause() {
            return this.cause;
        }

        public long getTimestamp() {
            return this.timestamp;
        }
    }
}

