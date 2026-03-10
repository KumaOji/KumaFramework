package com.kuma.cloud.rpc.client.autoconfigure;

import com.kuma.cloud.rpc.client.core.ClientBs;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * RpcClientAutoConfiguration — 自定义 RPC 客户端自动配置
 * <p>
 * 激活条件: kuma.rpc.client.enabled=true
 * 注册 {@link RpcClientProperties} 配置属性 Bean，
 * 并暴露一个 {@link ClientBs} 原型工厂 Bean 供业务层按需创建有类型代理。
 * <p>
 * 使用示例:
 * <pre>{@code
 * @Autowired ClientBs<?> rpcClientFactory;
 *
 * MyService service = ClientBs.newInstance()
 *     .serviceId("myService")
 *     .serviceInterface(MyService.class)
 *     .addresses(rpcClientProperties.getServerAddress())
 *     .timeout(rpcClientProperties.getTimeout())
 *     .reference();
 * }</pre>
 *
 * @author kuma
 * @version 2026.02
 */
@AutoConfiguration
@EnableConfigurationProperties(RpcClientProperties.class)
@ConditionalOnProperty(prefix = "kuma.rpc.client", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClientBs<?> rpcClientBs(RpcClientProperties properties) {
        return ClientBs.newInstance()
                .timeout(properties.getTimeout())
                .subscribe(properties.isSubscribe());
    }
}
