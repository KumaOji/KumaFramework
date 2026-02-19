/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.kafka.streams.processor.ProcessorContext
 *  org.apache.kafka.streams.processor.internals.ProcessorContextImpl
 */
package com.kuma.boot.mq.kafka.kafkaextend.stream.util;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.internals.ProcessorContextImpl;

public final class ProcessorContextUtil {
    private ProcessorContextUtil() {
    }

    public static String toLogString(ProcessorContext context) {
        String res = " \u8282\u70b9\u5c5e\u6027: application-id: " + context.applicationId();
        if (context instanceof ProcessorContextImpl) {
            res = res + " currentNode.name: " + ((ProcessorContextImpl)context).currentNode().name();
        }
        res = res + " topic: " + context.topic() + " offset: " + context.offset() + " taskId: " + String.valueOf(context.taskId());
        return res;
    }
}

