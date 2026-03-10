package com.kuma.cloud.tx.rm.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * TxRmAutoConfiguration — 分布式事务 RM 端自动配置
 * <p>
 * 激活条件: kuma.tx.enabled=true
 * 启用后自动注册 {@code @DistributedTransactional} 切面、
 * Netty 事务通道客户端及 Web 拦截器等组件。
 *
 * @author kuma
 * @version 2026.02
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "kuma.tx", name = "enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("com.kuma.cloud.tx.rm")
public class TxRmAutoConfiguration {
}
