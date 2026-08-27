package com.kuma.cloud.project9.service;

import com.kuma.cloud.mq.client.producer.core.MqProducer;
import com.kuma.cloud.mq.client.producer.dto.SendResult;
import com.kuma.cloud.mq.common.dto.req.MqMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 生产者服务
 * <p>
 * 封装 {@link MqProducer}，提供发送消息的业务接口。
 * 启动时先等待 Broker 连接就绪（10s），再对外提供发送能力。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqProducerService {

    private final MqProducer mqProducer;

    @PostConstruct
    public void init() throws InterruptedException {
        // 启动生产者线程（连接 Broker）
        mqProducer.start();
        // 等待连接建立
        Thread.sleep(3000L);
        log.info("[Producer] 生产者启动完成，可以发送消息");
    }

    /**
     * 发送单条消息
     *
     * @param payload  消息内容
     * @param tags     消息标签列表
     * @param bizKey   业务键（可为 null）
     * @return 发送结果
     */
    public SendResult send(String payload, List<String> tags, String bizKey) {
        MqMessage message = new MqMessage();
        message.setTopic("DEMO_TOPIC");
        message.setTags(tags);
        message.setPayload(payload);
        message.setBizKey(bizKey);

        SendResult result = mqProducer.send(message);
        log.info("[Producer] 发送消息: payload={}, status={}, messageId={}",
                payload, result.getStatus(), result.getMessageId());
        return result;
    }

    /**
     * 单向发送（不等待 Broker 确认，适合日志场景）
     *
     * @param payload 消息内容
     * @param tags    消息标签列表
     * @return 发送结果
     */
    public SendResult sendOneWay(String payload, List<String> tags) {
        MqMessage message = new MqMessage();
        message.setTopic("DEMO_TOPIC");
        message.setTags(tags);
        message.setPayload(payload);

        SendResult result = mqProducer.sendOneWay(message);
        log.info("[Producer] 单向发送消息: payload={}, status={}", payload, result.getStatus());
        return result;
    }
}
