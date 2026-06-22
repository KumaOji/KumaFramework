package com.kuma.boot.arthas.autoconfigure;

import com.kuma.boot.arthas.ArthasAgentBootstrap;
import com.kuma.boot.arthas.autoconfigure.properties.ArthasProperties;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.taobao.arthas.agent.attach.ArthasAgent;

@AutoConfiguration
@ConditionalOnClass(ArthasAgent.class)
@EnableConfigurationProperties({ArthasProperties.class})
@ConditionalOnProperty(prefix = ArthasProperties.PREFIX, name = "enabled", havingValue = "true")
public class ArthasAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(ArthasAutoConfiguration.class, "kuma-boot-starter-arthas");
    }

    @Bean
    @ConditionalOnMissingBean
    public ArthasAgentBootstrap arthasAgentBootstrap(ArthasProperties properties) {
        return new ArthasAgentBootstrap(properties);
    }
}
