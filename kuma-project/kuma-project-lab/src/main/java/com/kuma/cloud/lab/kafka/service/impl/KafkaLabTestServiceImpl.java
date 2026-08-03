package com.kuma.cloud.lab.kafka.service.impl;

import com.kuma.cloud.lab.kafka.config.KafkaLabProperties;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMessageEvent;
import com.kuma.cloud.lab.kafka.domain.dto.KafkaLabSendDTO;
import com.kuma.cloud.lab.kafka.domain.vo.KafkaLabSendVO;
import com.kuma.cloud.lab.kafka.service.KafkaLabEventStore;
import com.kuma.cloud.lab.kafka.service.KafkaLabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLabTestServiceImpl implements KafkaLabTestService {

    private final KafkaLabProperties properties;
    private final KafkaLabEventStore eventStore;
    private final ObjectProvider<KafkaTemplate<String, String>> kafkaLabTemplateProvider;

    @Override
    public KafkaLabSendVO send(KafkaLabSendDTO dto) {
        if (!properties.enabled()) {
            throw new IllegalStateException("Kafka lab test is disabled. Set kuma.lab.kafka.enabled=true first.");
        }
        KafkaTemplate<String, String> kafkaTemplate = kafkaLabTemplateProvider.getIfAvailable();
        if (kafkaTemplate == null) {
            throw new IllegalStateException("KafkaTemplate is not available.");
        }

        String topic = hasText(dto.getTopic()) ? dto.getTopic() : properties.topic();
        try {
            SendResult<String, String> sendResult = kafkaTemplate
                    .send(topic, dto.getKey(), dto.getMessage())
                    .get();
            KafkaLabMessageEvent event = eventStore.appendProduced(
                    sendResult.getRecordMetadata().topic(),
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset(),
                    dto.getKey(),
                    dto.getMessage()
            );
            return new KafkaLabSendVO(
                    event.id(),
                    event.topic(),
                    event.partition(),
                    event.offset()
            );
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            eventStore.markFailed(error);
            throw new IllegalStateException("Kafka send interrupted", error);
        } catch (Exception error) {
            eventStore.markFailed(error);
            throw new IllegalStateException("Kafka send failed: " + error.getMessage(), error);
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
