package com.kuma.cloud.lab.kafka.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Kafka 发送测试参数。
 */
@Data
public class KafkaLabSendDTO {

    private String key;

    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 可选，未指定时使用默认 topic。
     */
    private String topic;
}
