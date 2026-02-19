/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.kafka.clients.producer.ProducerRecord
 *  org.apache.kafka.clients.producer.RecordMetadata
 *  org.springframework.kafka.support.ProducerListener
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.mq.kafka.kafkause;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Component(value="kafkaSendResultHandler")
public class KafkaSendResultHandler
implements ProducerListener<String, Object> {
    public void onSuccess(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata) {
        if (recordMetadata != null) {
            LogUtils.info((String)"Kafka\u6d88\u606f\u53d1\u9001\u6210\u529f - \u4e3b\u9898: {}, \u5206\u533a: {}, \u504f\u79fb\u91cf: {}, \u952e: {}, \u503c: {}", (Object[])new Object[]{producerRecord.topic(), recordMetadata.partition(), recordMetadata.offset(), producerRecord.key(), producerRecord.value()});
        } else {
            LogUtils.warn((String)"Kafka\u6d88\u606f\u53d1\u9001\u6210\u529f\uff0c\u4f46RecordMetadata\u4e3anull - \u952e: {}, \u503c: {}", (Object[])new Object[]{producerRecord.key(), producerRecord.value()});
        }
    }

    public void onError(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        if (recordMetadata != null) {
            LogUtils.error((String)"Kafka\u6d88\u606f\u53d1\u9001\u5931\u8d25 - \u4e3b\u9898: {}, \u5206\u533a: {}, \u504f\u79fb\u91cf: {}, \u952e: {}, \u503c: {}, \u5f02\u5e38: {}", (Object[])new Object[]{producerRecord.topic(), recordMetadata.partition(), recordMetadata.offset(), producerRecord.key(), producerRecord.value(), exception.getMessage(), exception});
        } else {
            LogUtils.error((String)"Kafka\u6d88\u606f\u53d1\u9001\u5931\u8d25 - RecordMetadata\u4e3anull, \u952e: {}, \u503c: {}, \u5f02\u5e38: {}", (Object[])new Object[]{producerRecord.key(), producerRecord.value(), exception.getMessage(), exception});
        }
    }
}

