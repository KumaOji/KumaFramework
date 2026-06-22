package com.kuma.boot.jacoco.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.jacoco.autoconfigure.properties.JacocoProperties;
import com.kuma.boot.jacoco.service.JacocoCoverageService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * JaCoCo 代码覆盖率自动配置。
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties({JacocoProperties.class})
@ConditionalOnProperty(
        prefix = JacocoProperties.PREFIX,
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true)
public class JacocoAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JacocoAutoConfiguration.class, "kuma-boot-starter-jacoco", new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public JacocoCoverageService jacocoCoverageService(JacocoProperties properties) {
        return new JacocoCoverageService(properties);
    }
}
