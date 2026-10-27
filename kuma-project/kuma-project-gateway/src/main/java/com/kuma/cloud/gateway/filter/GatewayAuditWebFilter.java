package com.kuma.cloud.gateway.filter;

import com.kuma.boot.auditlog.core.AuditLogStore;
import com.kuma.boot.auditlog.enums.OperateType;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import com.kuma.cloud.gateway.properties.GatewayCloudProperties;
import com.kuma.cloud.gateway.support.ClientIpResolver;
import com.kuma.cloud.gateway.support.GatewayHeaders;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/** Audits completed mutations and rejected requests, without reading request or response bodies. */
public class GatewayAuditWebFilter implements WebFilter, Ordered {
    private static final String ACTOR = GatewayAuditWebFilter.class.getName() + ".actor";
    private static final Set<String> WRITES = Set.of("POST", "PUT", "PATCH", "DELETE");
    private final AuditLogStore store;
    private final GatewayCloudProperties properties;
    public GatewayAuditWebFilter(AuditLogStore store, GatewayCloudProperties properties) {
        this.store = store;
        this.properties = properties;
    }

    /** Installed inside Spring Security, after its authenticated exchange wrapper. */
    public Mono<Void> capturePrincipal(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal().doOnNext(principal -> {
            if (principal instanceof Authentication authentication && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)) {
                exchange.getAttributes().put(ACTOR, authentication.getName());
            }
        }).then(Mono.defer(() -> chain.filter(exchange)));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getRawPath();
        if (path.equals("/actuator") || path.startsWith("/actuator/")) return chain.filter(exchange);
        return Mono.defer(() -> {
            long start = System.nanoTime();
            var failure = new AtomicReference<Throwable>();
            return Mono.defer(() -> chain.filter(exchange)).doOnError(failure::set).doFinally(signal -> {
                Throwable error = failure.get();
                int status = signal == SignalType.CANCEL ? 499
                        : error instanceof ResponseStatusException response ? response.getStatusCode().value()
                        : error != null ? 500 : exchange.getResponse().getStatusCode() != null
                        ? exchange.getResponse().getStatusCode().value() : 200;
                String method = exchange.getRequest().getMethod().name();
                if (!WRITES.contains(method) && !path.endsWith("/logout") && status < 400) return;
                record(exchange, method, path, status, start);
            });
        });
    }

    private void record(ServerWebExchange exchange, String method, String path, int status, long start) {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        try {
            MDC.clear();
            MDC.setContextMap(exchange.getAttributeOrDefault(GatewayHeaders.MDC_ATTRIBUTE, Map.of()));
            var entry = new AuditLogEntry();
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            entry.setModule("gateway.http");
            entry.setType(switch (method) {
                case "POST" -> OperateType.CREATE;
                case "PUT", "PATCH" -> OperateType.UPDATE;
                case "DELETE" -> OperateType.DELETE;
                default -> OperateType.OTHER;
            });
            entry.setBizId(route == null ? null : route.getId());
            entry.setDescription("HTTP " + method + " " + status);
            entry.setMethod("gateway.request");
            entry.setOperatorId(exchange.getAttribute(ACTOR));
            entry.setOperatorName(exchange.getAttribute(ACTOR));
            entry.setSuccess(status < 400);
            entry.setTraceId(exchange.getAttribute(GatewayHeaders.TRACE_ID_ATTRIBUTE));
            entry.setRequestUri(path);
            entry.setHttpMethod(method);
            entry.setClientIp(ClientIpResolver.resolve(exchange.getRequest(), properties.isTrustProxyHeaders()));
            entry.setOperateTime(LocalDateTime.now());
            entry.setCostMillis((System.nanoTime() - start) / 1_000_000);
            store.save(entry);
        } catch (RuntimeException ignored) {
            LoggerFactory.getLogger(getClass()).warn("Gateway audit could not be recorded");
        } finally {
            MDC.clear();
            if (previous != null) MDC.setContextMap(previous);
        }
    }

    @Override public int getOrder() { return Ordered.HIGHEST_PRECEDENCE + 40; }
}
