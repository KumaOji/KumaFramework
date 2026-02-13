/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pipeline.pipeline;

public class PipelineException
extends Exception {
    private final String nodeName;

    public PipelineException(String nodeName, String message) {
        super(message);
        this.nodeName = nodeName;
    }

    public PipelineException(String nodeName, String message, Throwable cause) {
        super(message, cause);
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return this.nodeName;
    }
}

