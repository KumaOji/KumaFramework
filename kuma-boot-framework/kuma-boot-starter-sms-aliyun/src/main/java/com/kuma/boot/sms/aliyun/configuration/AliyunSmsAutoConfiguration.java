package com.kuma.boot.sms.aliyun.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.aliyun.handler.AliyunSendHandler;
import com.kuma.boot.sms.aliyun.properties.AliyunSmsProperties;
import com.kuma.boot.sms.common.condition.ConditionalOnSmsEnabled;
import com.kuma.boot.sms.common.loadbalancer.SmsSenderLoadBalancer;
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
 * 阿里云短信自动配置
 *
 * <p>生效条件：{@code kuma.boot.sms.enabled=true} 且配置了 {@code kuma.boot.sms.aliyun.access-key-id}。
 * 装配后将 {@link AliyunSendHandler} 注册到 sms-common 的 {@link SmsSenderLoadBalancer}，
 * 由 NoticeService 通过负载均衡 + 模板类型匹配选用，可与腾讯云等其它通道共存。
 */
@AutoConfiguration
@ConditionalOnSmsEnabled
@EnableConfigurationProperties({AliyunSmsProperties.class})
@ConditionalOnProperty(prefix = AliyunSmsProperties.PREFIX, name = "access-key-id")
public class AliyunSmsAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(AliyunSmsAutoConfiguration.class, "kuma-boot-starter-sms-aliyun", new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public AliyunSendHandler aliyunSendHandler(AliyunSmsProperties properties,
                                               ObjectProvider<ApplicationEventPublisher> eventPublisher) {
        return new AliyunSendHandler(properties, eventPublisher.getIfAvailable());
    }

    @Bean
    public SmartInitializingSingleton aliyunSendHandlerRegistrar(AliyunSendHandler handler,
                                                                 ObjectProvider<SmsSenderLoadBalancer> loadBalancer) {
        return () -> loadBalancer.ifAvailable(lb -> lb.addTarget(handler, true));
    }
}
