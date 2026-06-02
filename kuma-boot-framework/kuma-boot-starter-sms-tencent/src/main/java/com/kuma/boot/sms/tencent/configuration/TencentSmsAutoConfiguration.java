package com.kuma.boot.sms.tencent.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.condition.ConditionalOnSmsEnabled;
import com.kuma.boot.sms.common.loadbalancer.SmsSenderLoadBalancer;
import com.kuma.boot.sms.tencent.handler.TencentSendHandler;
import com.kuma.boot.sms.tencent.properties.TencentSmsProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

/**
 * 腾讯云短信自动配置
 *
 * <p>生效条件：{@code kuma.boot.sms.enabled=true} 且配置了 {@code kuma.boot.sms.tencent.secret-id}。
 * 装配后将 {@link TencentSendHandler} 注册到 sms-common 的 {@link SmsSenderLoadBalancer}，
 * 可与阿里云等其它通道共存，由负载均衡 + 模板类型匹配选用。
 */
@AutoConfiguration
@ConditionalOnSmsEnabled
@EnableConfigurationProperties({TencentSmsProperties.class})
@ConditionalOnProperty(prefix = TencentSmsProperties.PREFIX, name = "secret-id")
public class TencentSmsAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(TencentSmsAutoConfiguration.class, "kuma-boot-starter-sms-tencent", new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public TencentSendHandler tencentSendHandler(TencentSmsProperties properties,
                                                 ObjectProvider<ApplicationEventPublisher> eventPublisher) {
        return new TencentSendHandler(properties, eventPublisher.getIfAvailable());
    }

    @Bean
    public SmartInitializingSingleton tencentSendHandlerRegistrar(TencentSendHandler handler,
                                                                  ObjectProvider<SmsSenderLoadBalancer> loadBalancer) {
        return () -> loadBalancer.ifAvailable(lb -> lb.addTarget(handler, true));
    }
}
