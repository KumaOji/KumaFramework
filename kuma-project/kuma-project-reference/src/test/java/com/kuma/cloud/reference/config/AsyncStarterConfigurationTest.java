package com.kuma.cloud.reference.config;

import com.kuma.boot.core.autoconfigure.AsyncAutoConfiguration;
import com.kuma.boot.core.autoconfigure.AsyncThreadPoolAutoConfiguration;
import com.kuma.boot.core.support.thread.MDCThreadPoolTaskExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.task.TaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncStarterConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AsyncThreadPoolAutoConfiguration.class));

    @Test
    void preservesTheStartersCallerRunsDefault() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(ThreadPoolTaskExecutor.class);
            var executor = context.getBean(ThreadPoolTaskExecutor.class);
            assertThat(executor).isInstanceOf(MDCThreadPoolTaskExecutor.class);
            assertThat(executor.getThreadPoolExecutor().getRejectedExecutionHandler())
                    .isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
        });
    }

    @Test
    void backsOffWhenTheApplicationProvidesItsOwnExecutor() {
        runner.withUserConfiguration(CustomExecutorConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(ThreadPoolTaskExecutor.class);
            assertThat(context.getBean(ThreadPoolTaskExecutor.class).getThreadNamePrefix())
                    .isEqualTo("custom-");
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomExecutorConfiguration {
        @Bean
        ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor() {
            var executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("custom-");
            return executor;
        }
    }

    @Test
    void fullAsyncConfigurationStartsAndInvokesAdditionalDecoratorOnce() {
        var decorated = new AtomicInteger();
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(AsyncAutoConfiguration.class,
                        AsyncThreadPoolAutoConfiguration.class))
                .withBean("additionalTaskDecorator", TaskDecorator.class, () -> task -> () -> {
                    decorated.incrementAndGet();
                    task.run();
                })
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var executor = context.getBean("asyncThreadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
                    assertThat(executor.submit(() -> "completed").get(5, TimeUnit.SECONDS))
                            .isEqualTo("completed");
                    assertThat(decorated).hasValue(1);
                });
    }
}
