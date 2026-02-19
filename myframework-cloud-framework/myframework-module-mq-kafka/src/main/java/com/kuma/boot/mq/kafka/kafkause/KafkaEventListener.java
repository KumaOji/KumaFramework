/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.kafka.clients.consumer.ConsumerRecord
 *  org.springframework.kafka.annotation.KafkaListener
 *  org.springframework.kafka.support.Acknowledgment
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.mq.kafka.kafkause;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventListener {
    @KafkaListener(topics={"${slr-connector.kafka.consumer_topic}"}, groupId="${spring.kafka.consumer.group-id}", containerFactory="kafkaListenerContainerFactory", errorHandler="kafkaConsumerExceptionHandler")
    public void onAlert(List<ConsumerRecord<String, String>> consumerRecordList, Acknowledgment ack) {
        for (ConsumerRecord<String, String> record : consumerRecordList) {
            LogUtils.info((String)"Consumed message - Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}", (Object[])new Object[]{record.topic(), record.partition(), record.offset(), record.key(), record.value()});
        }
        ack.acknowledge();
    }
}

