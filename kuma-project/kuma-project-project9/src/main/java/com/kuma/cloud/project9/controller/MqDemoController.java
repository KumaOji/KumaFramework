package com.kuma.cloud.project9.controller;

import com.kuma.cloud.mq.client.producer.dto.SendResult;
import com.kuma.cloud.project9.service.MqProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息队列演示接口
 *
 * <p>发送流程：
 * <ol>
 *   <li>GET /mq/send — 发送单条消息（同步，等待 Broker 确认）</li>
 *   <li>GET /mq/send-one-way — 单向发送（不等待确认）</li>
 *   <li>GET /mq/send-batch — 批量发送</li>
 * </ol>
 * Consumer 在应用启动后自动订阅 DEMO_TOPIC，收到消息后打印日志。
 * </p>
 */
@RestController
@RequestMapping("/mq")
@RequiredArgsConstructor
public class MqDemoController {

    private final MqProducerService mqProducerService;

    /**
     * 同步发送单条消息
     *
     * <pre>GET /mq/send?payload=hello&bizKey=BK001</pre>
     */
    @GetMapping("/send")
    public String send(
            @RequestParam(defaultValue = "Hello MQ!") String payload,
            @RequestParam(defaultValue = "TAG_A")     String tag,
            @RequestParam(required = false)           String bizKey) {
        SendResult result = mqProducerService.send(payload, List.of(tag), bizKey);
        return "发送结果: status=" + result.getStatus() + ", messageId=" + result.getMessageId();
    }

    /**
     * 单向发送（fire-and-forget）
     *
     * <pre>GET /mq/send-one-way?payload=fire</pre>
     */
    @GetMapping("/send-one-way")
    public String sendOneWay(
            @RequestParam(defaultValue = "One-Way Message") String payload,
            @RequestParam(defaultValue = "TAG_B")           String tag) {
        SendResult result = mqProducerService.sendOneWay(payload, List.of(tag));
        return "单向发送结果: status=" + result.getStatus() + ", messageId=" + result.getMessageId();
    }

    /**
     * 批量发送（连续发送 count 条消息）
     *
     * <pre>GET /mq/send-batch?count=5</pre>
     */
    @GetMapping("/send-batch")
    public String sendBatch(
            @RequestParam(defaultValue = "5") int count) {
        StringBuilder sb = new StringBuilder("批量发送结果:\n");
        for (int i = 1; i <= count; i++) {
            SendResult result = mqProducerService.send(
                    "Batch Message #" + i, List.of("TAG_A", "TAG_B"), "BATCH-" + i);
            sb.append(i).append(". status=").append(result.getStatus())
              .append(", messageId=").append(result.getMessageId()).append("\n");
        }
        return sb.toString();
    }
}
