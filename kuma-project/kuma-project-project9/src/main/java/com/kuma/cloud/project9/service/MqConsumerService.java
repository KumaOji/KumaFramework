package com.kuma.cloud.project9.service;

import com.kuma.cloud.mq.client.consumer.api.MqConsumerListener;
import com.kuma.cloud.mq.client.consumer.api.MqConsumerListenerContext;
import com.kuma.cloud.mq.client.consumer.core.MqConsumerPush;


import com.kuma.cloud.mq.common.dto.req.MqMessage;
import com.kuma.cloud.mq.common.resp.ConsumerStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消费者服务
 * <p>
 * 启动后订阅 DEMO_TOPIC，将收到的消息打印到日志。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqConsumerService {

    private final MqConsumerPush mqConsumerPush;

    @PostConstruct
    public void init() {
        // 1. 启动消费者线程（连接 Broker）
        mqConsumerPush.start();

        // 2. 订阅主题（支持 tag 正则，* 表示全部）
        mqConsumerPush.subscribe("DEMO_TOPIC", "*");

        // 3. 注册消息监听器
        mqConsumerPush.registerListener(new MqConsumerListener() {
            @Override
            public ConsumerStatus consumer(MqMessage mqMessage, MqConsumerListenerContext context) {
                log.info("[Consumer] 收到消息: topic={}, tags={}, payload={}, bizKey={}",
                        mqMessage.getTopic(),
                        mqMessage.getTags(),
                        mqMessage.getPayload(),
                        mqMessage.getBizKey());
                return ConsumerStatus.SUCCESS;
            }
        });

        log.info("[Consumer] 已订阅 DEMO_TOPIC，等待消息...");
    }
}
