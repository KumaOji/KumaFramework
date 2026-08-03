package com.kuma.cloud.lab.kafka.service;

import com.kuma.cloud.lab.kafka.domain.dto.KafkaLabSendDTO;
import com.kuma.cloud.lab.kafka.domain.vo.KafkaLabSendVO;

public interface KafkaLabTestService {

    KafkaLabSendVO send(KafkaLabSendDTO dto);
}
