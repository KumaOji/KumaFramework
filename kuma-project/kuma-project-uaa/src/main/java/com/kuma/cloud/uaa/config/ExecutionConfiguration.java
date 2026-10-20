package com.kuma.cloud.uaa.config;

import com.kuma.boot.core.autoconfigure.AsyncThreadPoolAutoConfiguration;
import com.kuma.boot.logger.autoconfigure.SkyWalkingAsyncAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Explicitly loads the Kuma starter features used by this application. */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        AsyncThreadPoolAutoConfiguration.class,
        SkyWalkingAsyncAutoConfiguration.class
})
public class ExecutionConfiguration {
}
