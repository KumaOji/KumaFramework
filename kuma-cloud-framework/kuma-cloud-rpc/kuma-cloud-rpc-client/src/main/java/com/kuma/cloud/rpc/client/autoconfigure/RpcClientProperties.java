package com.kuma.cloud.rpc.client.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RpcClientProperties — 自定义 RPC 客户端配置属性
 *
 * @author kuma
 * @version 2026.02
 */
@Data
@ConfigurationProperties(prefix = "kuma.rpc.client")
public class RpcClientProperties {

    /** 是否启用 RPC 客户端，默认 false */
    private boolean enabled = false;

    /** 服务端地址，格式 host:port（逗号分隔多个） */
    private String serverAddress = "127.0.0.1:9527";

    /** 调用超时毫秒数，默认 60000 */
    private long timeout = 60000;

    /** 是否订阅注册中心进行自动发现，默认 false（直连模式） */
    private boolean subscribe = false;
}
