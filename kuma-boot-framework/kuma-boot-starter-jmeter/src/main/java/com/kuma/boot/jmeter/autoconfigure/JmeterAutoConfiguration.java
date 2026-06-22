package com.kuma.boot.jmeter.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.jmeter.autoconfigure.properties.JmeterProperties;
import com.kuma.boot.jmeter.service.JmeterTestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Apache JMeter 嵌入式压测自动配置。
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties({JmeterProperties.class})
@ConditionalOnProperty(
        prefix = JmeterProperties.PREFIX,
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true)
public class JmeterAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JmeterAutoConfiguration.class, "kuma-boot-starter-jmeter", new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean
    public JmeterTestService jmeterTestService(JmeterProperties properties) {
        return new JmeterTestService(properties);
    }
}
