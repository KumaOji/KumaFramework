package com.kuma.cloud.reference.config;

import com.kuma.boot.core.autoconfigure.AsyncThreadPoolAutoConfiguration;
import com.kuma.boot.core.autoconfigure.ClockAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.SkyWalkingAsyncAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({AsyncThreadPoolAutoConfiguration.class, SkyWalkingAsyncAutoConfiguration.class,
        ClockAutoConfiguration.class})
public class ExecutionConfiguration {}
