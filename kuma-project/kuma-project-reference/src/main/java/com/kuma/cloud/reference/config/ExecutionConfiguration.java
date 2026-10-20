package com.kuma.cloud.reference.config;

import com.kuma.boot.core.autoconfigure.AsyncThreadPoolAutoConfiguration;
import java.time.Clock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.core.task.TaskDecorator;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;

@EnableAsync
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(AsyncThreadPoolAutoConfiguration.class)
public class ExecutionConfiguration {
    @Bean
    TaskDecorator skywalkingTaskDecorator() {
        return RunnableWrapper::of;
    }

    @Bean
    Clock clock() { return Clock.systemUTC(); }
}
