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
    void nativeFallbackDoesNotInventTraceIdsWithoutAnAgent() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/no-agent"));
        StepVerifier.create(new GatewaySkyWalkingWebFilter().filter(exchange,
                request -> new GatewaySkyWalkingRelayGlobalFilter().filter(request,
                        forwarded -> forwarded.getResponse().setComplete()))).verifyComplete();
        assertThat(exchange.getAttributes()).doesNotContainKeys(
                GatewayHeaders.SKYWALKING_ID_ATTRIBUTE, GatewayHeaders.SKYWALKING_SNAPSHOT_ATTRIBUTE);
    }

    @Test
    void reactiveFailureLogsCarryRequestMdcAndRestoreTheCallingThread() {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(RequestLogGlobalFilter.class);
        var events = new CopyOnWriteArrayList<ch.qos.logback.classic.spi.ILoggingEvent>();
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            @Override
            protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
                event.prepareForDeferredProcessing();
                events.add(event);
            }
        };
        appender.start();
        logger.addAppender(appender);
        var previous = org.slf4j.MDC.getCopyOfContextMap();
        org.slf4j.MDC.put("sentinel", "original");
        try {
            var factory = new StaticListableBeanFactory();
            var trace = new GatewayTraceGlobalFilter(factory.getBeanProvider(io.micrometer.tracing.Tracer.class));
            var access = new RequestLogGlobalFilter(new GatewayCloudProperties(),
                    factory.getBeanProvider(com.kuma.boot.ip2region.model.Ip2regionSearcher.class));
            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/failure")
                    .header(CommonConstants.KMC_TRACE_ID, "gateway-error")
                    .header(CommonConstants.KMC_TENANT_ID, "demo-tenant")
                    .header(CommonConstants.KMC_REQUEST_VERSION, "v2"));
            StepVerifier.create(trace.filter(exchange, traced -> access.filter(traced,
                    logged -> Mono.error(new IllegalStateException("upstream failed")))))
                    .expectErrorMessage("upstream failed").verify();
            assertThat(events).hasSize(1);
            assertThat(events.getFirst().getMDCPropertyMap())
                    .containsEntry(CommonConstants.KMC_TRACE_ID, "gateway-error")
                    .containsEntry(CommonConstants.KMC_TENANT_ID, "demo-tenant")
                    .containsEntry(CommonConstants.KMC_REQUEST_VERSION, "v2")
                    .doesNotContainKey("sentinel");
            assertThat(org.slf4j.MDC.get("sentinel")).isEqualTo("original");
            assertThat(org.slf4j.MDC.get(CommonConstants.KMC_TRACE_ID)).isNull();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            if (previous == null) org.slf4j.MDC.clear();
            else org.slf4j.MDC.setContextMap(previous);
        }
    }

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
