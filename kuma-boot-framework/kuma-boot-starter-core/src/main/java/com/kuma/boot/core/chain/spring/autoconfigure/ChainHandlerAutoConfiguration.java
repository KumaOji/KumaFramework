package com.kuma.boot.core.chain.spring.autoconfigure;

import com.kuma.boot.core.chain.core.executor.ChainExecutor;
import com.kuma.boot.core.chain.core.registry.ChainRegistry;
import com.kuma.boot.core.chain.spring.properties.ChainHandlerProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 责任链处理者自动配置类
 */
@Configuration
@ComponentScan(basePackages = "com.kuma.boot.core.chain.spring")
@EnableConfigurationProperties(ChainHandlerProperties.class)
public class ChainHandlerAutoConfiguration {

    /**
     * 创建链注册器Bean
     *
     * @param <P> Param类型
     * @param <R> Response类型
     * @return 链注册器
     */
    @Bean
    @ConditionalOnMissingBean
    public <P, R> ChainRegistry<P, R> chainRegistry() {
        return new ChainRegistry<>();
    }

    /**
     * 创建链执行器Bean
     *
     * @param chainRegistry   链注册器
     * @param executorService 线程池
     * @param <P>             Param类型
     * @param <R>             Response类型
     * @return 链执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public <P, R> ChainExecutor<P, R> chainExecutor(ChainRegistry<P, R> chainRegistry,
                                                    @Qualifier("handlerExecutorService") ExecutorService executorService) {
        return new ChainExecutor<>(chainRegistry, executorService);
    }

    /**
     * 创建线程池Bean
     *
     * @param properties 配置属性
     * @return 线程池
     */
    @Bean
    @ConditionalOnMissingBean
    public ExecutorService handlerExecutorService(ChainHandlerProperties properties) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueCapacity()),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("chain-handler-" + t.threadId());
                    t.setDaemon(false);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return executor;
    }
}
