package com.kuma.boot.mq.kafka.kafkause;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaEventListener {

    @KafkaListener(
            topics = "${slr-connector.kafka.consumer_topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory",
            errorHandler = "kafkaConsumerExceptionHandler"
    )
    public void onAlert(List<ConsumerRecord<String, String>> consumerRecordList, Acknowledgment ack) {
        for (ConsumerRecord<String, String> record : consumerRecordList) {
            // 打印消费的详细信息
            LogUtils.info("Consumed message - Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
                    record.value());
        }
        // 手动提交偏移量
        ack.acknowledge();
    }
}
