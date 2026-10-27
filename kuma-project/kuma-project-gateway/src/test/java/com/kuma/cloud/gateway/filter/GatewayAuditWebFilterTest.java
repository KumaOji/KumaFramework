package com.kuma.cloud.gateway.filter;

import com.kuma.boot.auditlog.model.AuditLogEntry;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.GatewayHeaders;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.*;

class GatewayAuditWebFilterTest {
    @Test
    void recordsAfterCompletionAndUsesVerifiedPrincipalInsteadOfHeaders() {
        var records = new ArrayList<AuditLogEntry>();
        var filter = new GatewayAuditWebFilter(records::add, new GatewayCloudProperties());
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/blog/article?token=secret")
                .header("X-User-Id", "forged-user").header("Authorization", "Bearer secret"))
                .mutate().principal(Mono.just(UsernamePasswordAuthenticationToken.authenticated("verified-user", "secret", java.util.List.of())))
                .build();
        exchange.getAttributes().put(GatewayHeaders.TRACE_ID_ATTRIBUTE, "trace-1");
        exchange.getAttributes().put(GatewayHeaders.MDC_ATTRIBUTE, Map.of("traceId", "trace-1"));
        StepVerifier.create(filter.filter(exchange, outer -> filter.capturePrincipal(outer, inner -> Mono.defer(() -> {
            assertThat(records).isEmpty();
            inner.getResponse().setStatusCode(HttpStatus.CREATED);
            return inner.getResponse().setComplete();
        })))).verifyComplete();
        assertThat(records).hasSize(1);
        var audit = records.getFirst();
        assertThat(audit.isSuccess()).isTrue();
        assertThat(audit.getOperatorId()).isEqualTo("verified-user");
        assertThat(audit.getRequestUri()).isEqualTo("/blog/article");
        assertThat(audit.getDescription()).isEqualTo("HTTP POST 201");
        assertThat(audit.getTraceId()).isEqualTo("trace-1");
        assertThat(audit.getParams()).isNull();
        assertThat(audit.getResult()).isNull();
    }

    @Test
    void capturesSecurityRejectionErrorsAndCancellationExactlyOnce() {
        var records = new ArrayList<AuditLogEntry>();
        var filter = new GatewayAuditWebFilter(records::add, new GatewayCloudProperties());
        var denied = MockServerWebExchange.from(MockServerHttpRequest.get("/private?password=secret").header("X-User-Id", "fake"));
        StepVerifier.create(filter.filter(denied, ex -> {
            ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return ex.getResponse().setComplete();
        })).verifyComplete();
        assertThat(records.getFirst().isSuccess()).isFalse();
        assertThat(records.getFirst().getOperatorId()).isNull();
        var failed = MockServerWebExchange.from(MockServerHttpRequest.post("/failure"));
        StepVerifier.create(filter.filter(failed, ex -> Mono.error(new IllegalStateException("secret"))))
                .expectErrorMessage("secret").verify();
        assertThat(records.get(1).getDescription()).isEqualTo("HTTP POST 500");
        assertThat(records.get(1).getErrorMsg()).isNull();
        var cancelled = MockServerWebExchange.from(MockServerHttpRequest.post("/cancelled"));
        StepVerifier.create(filter.filter(cancelled, ex -> Mono.never())).thenCancel().verify();
        assertThat(records).hasSize(3);
        assertThat(records.get(2).getDescription()).isEqualTo("HTTP POST 499");
        assertThat(records.get(2).isSuccess()).isFalse();
    }

    @Test
    void skipsSuccessfulReadsAndActuatorAndDoesNotFailRequestsWhenStoreFails() {
        var records = new ArrayList<AuditLogEntry>();
        var filter = new GatewayAuditWebFilter(records::add, new GatewayCloudProperties());
        StepVerifier.create(filter.filter(MockServerWebExchange.from(MockServerHttpRequest.get("/blog/article/list")),
                ex -> ex.getResponse().setComplete())).verifyComplete();
        StepVerifier.create(filter.filter(MockServerWebExchange.from(MockServerHttpRequest.post("/actuator/refresh")),
                ex -> ex.getResponse().setComplete())).verifyComplete();
        assertThat(records).isEmpty();
        var unavailable = new GatewayAuditWebFilter(entry -> { throw new IllegalStateException("offline"); }, new GatewayCloudProperties());
        StepVerifier.create(unavailable.filter(MockServerWebExchange.from(MockServerHttpRequest.post("/write")),
                ex -> ex.getResponse().setComplete())).verifyComplete();
    }
}
