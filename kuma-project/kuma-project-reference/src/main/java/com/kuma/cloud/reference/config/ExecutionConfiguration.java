package com.kuma.cloud.reference.config;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration(proxyBeanMethods = false)
public class ExecutionConfiguration {
    @Bean
    Clock clock() { return Clock.systemUTC(); }

    /** 与 Tomcat 请求线程、Netty I/O 线程、HikariCP 连接池各自独立。 */
    @Bean(name = {"businessExecutor", "applicationTaskExecutor", "taskExecutor", "asyncThreadPoolTaskExecutor"})
    ThreadPoolTaskExecutor businessExecutor(BusinessExecutorProperties properties) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("reference-business-");
        executor.setCorePoolSize(properties.coreSize());
        executor.setMaxPoolSize(properties.maxSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setKeepAliveSeconds(properties.keepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(properties.shutdownSeconds());
        executor.setStrictEarlyShutdown(true);
        executor.setTaskDecorator(task -> {
            Map<String, String> submittingContext = MDC.getCopyOfContextMap();
            return () -> {
                var previous = MDC.getCopyOfContextMap();
                try {
                    restoreMdc(submittingContext);
                    task.run();
                } finally {
                    restoreMdc(previous);
                }
            };
        });
        return executor;
    }

    private static void restoreMdc(Map<String, String> context) {
        if (context == null) MDC.clear(); else MDC.setContextMap(context);
    }
}
