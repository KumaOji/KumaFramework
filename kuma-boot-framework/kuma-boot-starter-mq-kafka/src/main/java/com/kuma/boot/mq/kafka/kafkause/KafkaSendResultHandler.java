package com.kuma.boot.mq.kafka.kafkause;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Component("kafkaSendResultHandler")
public class KafkaSendResultHandler implements ProducerListener<Object, Object> {
    @Override
    public void onSuccess(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata) {
        // 记录成功发送的消息信息
        if (recordMetadata != null) {
            LogUtils.info("Kafka消息发送成功 - 主题: {}, 分区: {}, 偏移量: {}, 键: {}, 值: {}",
                    producerRecord.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset(),
                    producerRecord.key(),
                    producerRecord.value());
        } else {
            LogUtils.warn("Kafka消息发送成功，但RecordMetadata为null - 键: {}, 值: {}",
                    producerRecord.key(),
                    producerRecord.value());
        }
    }

    @Override
    public void onError(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        // 记录发送失败的消息信息及异常
        if (recordMetadata != null) {
            LogUtils.error("Kafka消息发送失败 - 主题: {}, 分区: {}, 偏移量: {}, 键: {}, 值: {}, 异常: {}",
                    producerRecord.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset(),
                    producerRecord.key(),
                    producerRecord.value(),
                    exception.getMessage(), exception);
        } else {
            LogUtils.error("Kafka消息发送失败 - RecordMetadata为null, 键: {}, 值: {}, 异常: {}",
                    producerRecord.key(),
                    producerRecord.value(),
                    exception.getMessage(), exception);
        }
    }
}
