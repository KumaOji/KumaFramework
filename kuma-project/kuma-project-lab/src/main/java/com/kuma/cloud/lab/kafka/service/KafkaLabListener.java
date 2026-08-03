package com.kuma.cloud.lab.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kuma.lab.kafka", name = "enabled", havingValue = "true")
public class KafkaLabListener {

    private final KafkaLabEventStore eventStore;

    @KafkaListener(
            topics = "${kuma.lab.kafka.topic}",
            groupId = "${kuma.lab.kafka.group-id}",
            containerFactory = "kafkaLabListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        eventStore.markListening(true);
        eventStore.appendConsumed(
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value()
        );
        log.info("Kafka lab consumed topic={}, partition={}, offset={}, key={}",
                record.topic(), record.partition(), record.offset(), record.key());
    }
}
