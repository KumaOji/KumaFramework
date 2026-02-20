package com.kuma.boot.mq.kafka.kafkause;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProvider {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${slr-connector.kafka.provider_topic}")
    private String providerTopic;

    /**
     * 批量上送事件信息到kafka
     */
//    public void batchSend(List<KafkaEventDTO> kafkaEventDTOList) {
//        if (CollectionUtils.isEmpty(kafkaEventDTOList)) {
//            return;
//        }
//        kafkaEventDTOList.forEach(eventUploadDTO -> kafkaTemplate.send(providerTopic, eventUploadDTO));
//    }
}
