package com.kuma.boot.monitor.monitor.monitor.dubbo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.apache.dubbo.config.spring.ServiceBean")
@ConditionalOnProperty(
   name = {"actuator.rpc.dubbo.enhance.enable"},
   havingValue = "true",
   matchIfMissing = true
)
public class DubboMetricsConfiguration {

    @Bean
    public DubboMetricsBeanPostProcessor dubboMetricsBeanPostProcessor() {
        return new DubboMetricsBeanPostProcessor();
    }
}
