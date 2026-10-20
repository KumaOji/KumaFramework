package com.kuma.boot.logger.autoconfigure;

import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;

/** Extends the core executor's context decorators with SkyWalking Agent propagation. */
@AutoConfiguration
public class SkyWalkingAsyncAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "skywalkingTaskDecorator")
    public TaskDecorator skywalkingTaskDecorator() {
        return RunnableWrapper::of;
    }
}
