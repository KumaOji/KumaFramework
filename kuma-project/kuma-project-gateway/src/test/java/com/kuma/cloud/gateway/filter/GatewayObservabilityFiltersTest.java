package com.kuma.cloud.gateway.filter;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.GatewayHeaders;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayObservabilityFiltersTest {
    @Test
    void keepsConcurrentRequestIdsIsolatedAcrossThreadSwitchesAndCommitsTimingHeader() {
        var factory = new StaticListableBeanFactory();
        var trace = new GatewayTraceGlobalFilter(factory.getBeanProvider(io.micrometer.tracing.Tracer.class));
        var access = new RequestLogGlobalFilter(new GatewayCloudProperties(),
                factory.getBeanProvider(com.kuma.boot.ip2region.model.Ip2regionSearcher.class));
        var observed = new CopyOnWriteArrayList<String>();
        StepVerifier.create(Flux.range(0, 30).flatMap(index -> {
            String id = "request-" + index;
            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/missing")
                    .header(CommonConstants.KMC_TRACE_ID, id));
            return trace.filter(exchange, traced -> access.filter(traced, logged ->
                    Mono.deferContextual(context -> {
                        assertThat(context.get(CommonConstants.KMC_TRACE_ID).toString()).isEqualTo(id);
                        assertThat(logged.getRequest().getHeaders().getFirst(CommonConstants.KMC_TRACE_ID)).isEqualTo(id);
                        observed.add(logged.getAttribute(GatewayHeaders.TRACE_ID_ATTRIBUTE));
                        logged.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                        return logged.getResponse().setComplete();
                    }).subscribeOn(Schedulers.parallel())))
                    .then(Mono.fromRunnable(() -> {
                        assertThat(exchange.getResponse().getHeaders().getFirst(CommonConstants.KMC_TRACE_ID)).isEqualTo(id);
                        assertThat(exchange.getResponse().getHeaders().getFirst(GatewayHeaders.RESPONSE_TIME)).endsWith("ms");
                    }));
        })).verifyComplete();
        assertThat(observed).hasSize(30).doesNotHaveDuplicates();
    }

    @Test
    void replacesUnboundedClientCorrelationIds() {
        var factory = new StaticListableBeanFactory();
        var trace = new GatewayTraceGlobalFilter(factory.getBeanProvider(io.micrometer.tracing.Tracer.class));
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/missing")
                .header(CommonConstants.KMC_TRACE_ID, "x".repeat(300)));
        StepVerifier.create(trace.filter(exchange, traced -> {
            assertThat(traced.getRequest().getHeaders().getFirst(CommonConstants.KMC_TRACE_ID)).matches("[0-9a-f]{32}");
            return traced.getResponse().setComplete();
        })).verifyComplete();
    }
}
