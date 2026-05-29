package com.kuma.boot.ai.autoconfigure;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.ai.service.impl.AiChatServiceImpl;
import com.kuma.boot.ai.service.impl.RagComponent;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@AutoConfiguration
@EnableConfigurationProperties(AiChatProperties.class)
public class AiChatAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(AiChatAutoConfiguration.class, StarterNameConstants.AI_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean
    public RagComponent ragComponent(AiChatProperties properties) {
        return new RagComponent(properties);
    }

    @Bean(name = "aiAsyncExecutor")
    @ConditionalOnMissingBean(name = "aiAsyncExecutor")
    public Executor aiAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(AiChatService.class)
    public AiChatService aiChatService(AiChatProperties properties, RagComponent ragComponent,
                                        @Qualifier("aiAsyncExecutor") Executor aiAsyncExecutor) {
        return new AiChatServiceImpl(properties, aiAsyncExecutor, ragComponent);
    }
}
