/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.kafka.clients.consumer.Consumer
 *  org.springframework.kafka.listener.KafkaListenerErrorHandler
 *  org.springframework.kafka.listener.ListenerExecutionFailedException
 *  org.springframework.messaging.Message
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.mq.kafka.kafkause;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component(value="kafkaConsumerExceptionHandler")
public class KafkaConsumerExceptionHandler
implements KafkaListenerErrorHandler {
    public Object handleError(Message<?> message, ListenerExecutionFailedException e) {
        LogUtils.error((String)"kafka\u6d88\u8d39\u6d88\u606f\u65f6\u53d1\u751f\u9519\u8bef\u3002\u6d88\u606f\u5185\u5bb9: {}, \u9519\u8bef\u4fe1\u606f: {}", (Object[])new Object[]{message, e.getMessage(), e});
        return null;
    }

    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
        LogUtils.error((String)"kafka\u6d88\u8d39\u6d88\u606f\u65f6\u53d1\u751f\u9519\u8bef\u3002\u6d88\u606f\u5185\u5bb9: {}, \u9519\u8bef\u4fe1\u606f: {}", (Object[])new Object[]{message, exception.getMessage(), exception});
        return null;
    }
}

