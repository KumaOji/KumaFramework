package com.kuma.cloud.lab.kafka.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KafkaScenarioDTO {
    @Min(2) @Max(6)
    private int partitions = 3;
    @Min(4) @Max(20)
    private int messageCount = 6;
    @Min(1) @Max(3)
    private short replicationFactor = 1;
    @Size(max = 1024)
    private String message = "Hello Kafka\n观察生产、消费和 offset 提交";
    private boolean demonstrateRebalance = true;
}
