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
 * subscribe() 需要与 Broker 网络交互（阻塞等待响应），不能放在 @PostConstruct
 * 里直接调用，否则会卡住 Spring 初始化线程导致 HTTP 服务无法接收请求。
 * 正确做法：用独立线程执行订阅，@PostConstruct 立即返回。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqConsumerService {

    private final MqConsumerPush mqConsumerPush;

    @PostConstruct
    public void init() {
        // 先注册监听器（非阻塞，直接加入内存列表）
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

        // 连接 Broker + 订阅均在独立线程执行，不阻塞 Spring 初始化
        new Thread(() -> {
            mqConsumerPush.start();          // 连接 Broker、发送 C_REGISTER（阻塞等响应）
            mqConsumerPush.subscribe("DEMO_TOPIC", "*");  // 发送 C_SUBSCRIBE（阻塞等响应）
            log.info("[Consumer] 已订阅 DEMO_TOPIC，等待消息...");
        }, "mq-consumer-init").start();

        log.info("[Consumer] 消费者初始化线程已启动，后台连接 Broker 中...");
    }
}
