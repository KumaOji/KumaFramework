package com.kuma.cloud.lab.kafka.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMessageEvent;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMonitorStatus;
import com.kuma.cloud.lab.kafka.domain.dto.KafkaLabSendDTO;
import com.kuma.cloud.lab.kafka.domain.vo.KafkaLabSendVO;
import com.kuma.cloud.lab.kafka.service.KafkaLabEventStore;
import com.kuma.cloud.lab.kafka.service.KafkaLabTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Kafka 测试")
@RestController
@RequestMapping("/lab/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaLabTestService kafkaLabTestService;
    private final KafkaLabEventStore eventStore;

    @Operation(summary = "发送一条 Kafka 测试消息")
    @PostMapping("/send")
    public Result<KafkaLabSendVO> send(@Valid @RequestBody KafkaLabSendDTO dto) {
        return Result.success(kafkaLabTestService.send(dto));
    }

    @Operation(summary = "获取 Kafka 监听器状态")
    @GetMapping("/status")
    public Result<KafkaLabMonitorStatus> status() {
        return Result.success(eventStore.status());
    }

    @Operation(summary = "获取最近的生产/消费消息")
    @GetMapping("/messages")
    public Result<List<KafkaLabMessageEvent>> messages(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) KafkaLabMessageEvent.Direction direction
    ) {
        return Result.success(eventStore.latest(limit, direction));
    }
}
