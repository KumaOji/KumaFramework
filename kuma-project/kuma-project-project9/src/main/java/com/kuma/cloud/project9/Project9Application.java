package com.kuma.cloud.project9;

import com.kuma.boot.core.application.StartupSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 消息队列 Demo
 * <p>
 * 演示 kuma-cloud-mq-client 的接入：
 * 1. 启动 kuma-cloud-mq-broker（Broker，Netty 监听 9999）
 * 2. 本应用作为生产者和消费者，通过 MqProducer / MqConsumerPush 收发消息
 * </p>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.kuma.boot", "com.kuma.cloud.project9", "com.kuma.cloud.mq.client"})
public class Project9Application {

    public static void main(String[] args) {
        new StartupSpringApplication(Project9Application.class)
                .setKmcBanner()
                .setKmcProfileIfNotExists("dev")
                .setKmcApplicationProperty("kuma-cloud-project9")
                .run(args);
    }
}
