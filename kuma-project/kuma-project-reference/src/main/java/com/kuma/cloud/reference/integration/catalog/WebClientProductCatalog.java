package com.kuma.cloud.reference.integration.catalog;

import com.kuma.cloud.reference.exception.CatalogException;
import com.kuma.cloud.reference.domain.model.Money;
import com.kuma.cloud.reference.domain.model.ProductSnapshot;
import com.kuma.cloud.reference.config.CatalogClientProperties;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientProductCatalog {
    private final WebClient client;
    private final CatalogClientProperties properties;

    public WebClientProductCatalog(WebClient catalogWebClient, CatalogClientProperties properties) {
        this.client = catalogWebClient;
        this.properties = properties;
    }

    public ProductSnapshot findBySku(String sku) {
        // 仅在 MVC/业务线程阻塞；不能在 Netty 事件循环或数据库事务中执行。
        return client.get().uri(builder -> builder.path("/products/{sku}").build(sku))
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> response.releaseBody()
                        .thenReturn(new CatalogException(CatalogException.Reason.NOT_FOUND, "商品不存在：" + sku, null)))
                .onStatus(HttpStatusCode::isError, response -> response.releaseBody()
                        .thenReturn(new CatalogException(CatalogException.Reason.UNAVAILABLE, "商品服务暂时不可用", null)))
                .bodyToMono(CatalogProduct.class)
                .switchIfEmpty(Mono.error(new CatalogException(CatalogException.Reason.UNAVAILABLE, "商品服务返回空响应", null)))
                .map(product -> {
                    if (!sku.equals(product.sku())) throw new IllegalArgumentException("商品服务返回了不匹配的 SKU");
                    return new ProductSnapshot(product.sku(), product.name(), new Money(product.unitPrice()));
                })
                // 总预算包含连接池排队、建连、响应和解码，避免只有 socket 超时而无整体期限。
                .timeout(properties.requestTimeout())
                .onErrorMap(error -> !(error instanceof CatalogException), this::translate)
                .block();
    }

    private CatalogException translate(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof TimeoutException || cause instanceof ReadTimeoutException || cause instanceof ConnectTimeoutException) {
                return new CatalogException(CatalogException.Reason.TIMEOUT, "商品服务请求超时", error);
            }
        }
        return new CatalogException(CatalogException.Reason.UNAVAILABLE, "商品服务响应异常或暂时不可用", error);
    }

    private record CatalogProduct(String sku, String name, BigDecimal unitPrice) {}
}
