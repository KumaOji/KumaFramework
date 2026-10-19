package com.kuma.cloud.reference.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("reference.catalog")
public record CatalogClientProperties(@NotNull URI baseUrl, @NotNull Duration connectTimeout,
        @NotNull Duration responseTimeout, @NotNull Duration requestTimeout,
        @NotNull Duration pendingAcquireTimeout, @NotNull Duration maxIdleTime,
        @NotNull Duration maxLifeTime, @Min(1) int maxConnections,
        @Min(1) int maxPendingAcquires, @Min(1024) int maxInMemorySize) {
    public CatalogClientProperties {
        if (baseUrl == null || !List.of("http", "https").contains(baseUrl.getScheme())
                || baseUrl.getHost() == null || baseUrl.getUserInfo() != null
                || baseUrl.getQuery() != null || baseUrl.getFragment() != null) {
            throw new IllegalArgumentException("商品服务地址必须是无内嵌凭据的 HTTP/HTTPS 地址");
        }
        for (Duration duration : new Duration[]{connectTimeout, responseTimeout, requestTimeout,
                pendingAcquireTimeout, maxIdleTime, maxLifeTime}) {
            if (duration == null || duration.toMillis() < 1 || duration.toMillis() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("HTTP 超时及连接存活时间必须在 1ms 到 2147483647ms 之间");
            }
        }
    }
}
