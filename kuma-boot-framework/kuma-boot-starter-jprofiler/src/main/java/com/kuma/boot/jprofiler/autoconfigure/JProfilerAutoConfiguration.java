package com.kuma.boot.jprofiler.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.jprofiler.JProfilerService;
import com.kuma.boot.jprofiler.autoconfigure.properties.JProfilerProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({JProfilerProperties.class})
@ConditionalOnProperty(prefix = JProfilerProperties.PREFIX, name = "enabled", havingValue = "true")
public class JProfilerAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(JProfilerAutoConfiguration.class, "kuma-boot-starter-jprofiler");
    }

    @Bean
    @ConditionalOnMissingBean
    public JProfilerService jProfilerService(JProfilerProperties properties) {
        return new JProfilerService(properties);
    }
}
