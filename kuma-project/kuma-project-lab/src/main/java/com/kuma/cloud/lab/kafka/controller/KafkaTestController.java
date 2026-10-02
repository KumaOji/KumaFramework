package com.kuma.cloud.lab.kafka.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMessageEvent;
import com.kuma.cloud.lab.kafka.domain.KafkaLabMonitorStatus;
import com.kuma.cloud.lab.kafka.domain.dto.KafkaLabSendDTO;
import com.kuma.cloud.lab.kafka.domain.dto.KafkaScenarioDTO;
import com.kuma.cloud.lab.kafka.domain.vo.KafkaLabSendVO;
import com.kuma.cloud.lab.kafka.service.KafkaLabEventStore;
import com.kuma.cloud.lab.kafka.service.KafkaLabTestService;
import com.kuma.cloud.lab.kafka.service.KafkaScenarioService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Tag(name = "Kafka 测试")
@RestController
@RequestMapping("/lab/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaLabTestService kafkaLabTestService;
    private final KafkaLabEventStore eventStore;
    private final KafkaScenarioService scenarioService;

    @Operation(summary = "启动 Kafka 完整实验：分区、生产、消费、提交、重放与组分配")
    @PostMapping("/scenario")
    public Result<Map<String, Object>> scenario(@Valid @RequestBody KafkaScenarioDTO dto) {
        return Result.success(scenarioService.start(dto));
    }

    @Operation(summary = "查询实验进度、逐步证据和验证结果")
    @GetMapping("/experiments/{id}")
    public Result<Map<String, Object>> experiment(@PathVariable String id) {
        return Result.success(scenarioService.get(id));
    }

    @Operation(summary = "最近 20 次 Kafka 实验")
    @GetMapping("/experiments")
    public Result<List<Map<String, Object>>> experiments() { return Result.success(scenarioService.latest()); }

    @Operation(summary = "清理指定已结束实验的独立 topic 与消费组")
    @DeleteMapping("/experiments/{id}")
    public Result<Map<String, Object>> cleanup(@PathVariable String id) throws Exception {
        return Result.success(scenarioService.cleanup(id));
    }

    @Operation(summary = "Kafka 集群：bootstrap、实际 broker、controller 与 topic 列表")
    @GetMapping("/cluster")
    public Result<Map<String, Object>> cluster() throws Exception { return Result.success(scenarioService.cluster()); }

    @Operation(summary = "Topic 详情：leader、replicas、ISR、起止 offset、提交位置与 lag")
    @GetMapping("/topic")
    public Result<Map<String, Object>> topic(@RequestParam String topic,
            @RequestParam(required = false) String groupId) throws Exception { return Result.success(scenarioService.topic(topic, groupId)); }

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
