package com.kuma.cloud.mq.client.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MqClientProperties — 自定义 MQ 客户端配置属性
 *
 * @author kuma
 * @version 2026.02
 */
@Data
@ConfigurationProperties(prefix = "kuma.mq.client")
public class MqClientProperties {

    /** 是否启用 MQ 客户端，默认 false */
    private boolean enabled = false;

    /** Broker 地址，格式 host:port */
    private String brokerAddress = "127.0.0.1:9999";

    /** 消费/生产者分组名 */
    private String groupName = "defaultGroup";

    /** 账户标识（可选） */
    private String appKey = "";

    /** 账户密码（可选） */
    private String appSecret = "";

    /** 响应超时毫秒数，默认 5000 */
    private long respTimeoutMills = 5000;

    /** 启动时是否检测 Broker 可用性，默认 true */
    private boolean check = true;
}
