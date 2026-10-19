package com.kuma.cloud.reference.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

@Configuration(proxyBeanMethods = false)
public class HttpClientConfiguration {
    /** 专用连接池及事件循环由 Spring 关闭，避免创建每次请求一个客户端。 */
    @Bean
    ReactorResourceFactory catalogResources(CatalogClientProperties properties) {
        var resources = new ReactorResourceFactory();
        resources.setUseGlobalResources(false);
        resources.setConnectionProviderSupplier(() -> ConnectionProvider.builder("reference-catalog")
                .maxConnections(properties.maxConnections())
                .pendingAcquireMaxCount(properties.maxPendingAcquires())
                .pendingAcquireTimeout(properties.pendingAcquireTimeout())
                .maxIdleTime(properties.maxIdleTime())
                .maxLifeTime(properties.maxLifeTime())
                .evictInBackground(Duration.ofSeconds(30))
                .build());
        resources.setLoopResourcesSupplier(() -> LoopResources.create("reference-http", 2, true));
        resources.setShutdownQuietPeriod(Duration.ZERO);
        resources.setShutdownTimeout(Duration.ofSeconds(5));
        return resources;
    }

    @Bean
    WebClient catalogWebClient(WebClient.Builder builder, ReactorResourceFactory catalogResources,
                              CatalogClientProperties properties) {
        var connector = new ReactorClientHttpConnector(catalogResources, client -> client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(properties.connectTimeout().toMillis()))
                .responseTimeout(properties.responseTimeout())
                .followRedirect(false));
        // 使用 Boot 注入的 Builder，保留框架的 codecs、观测等定制。
        return builder.clone().baseUrl(properties.baseUrl().toString()).clientConnector(connector)
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(properties.maxInMemorySize()))
                .build();
    }
}
