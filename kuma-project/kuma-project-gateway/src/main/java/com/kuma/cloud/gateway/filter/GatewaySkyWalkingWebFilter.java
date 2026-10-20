package com.kuma.cloud.gateway.filter;

import com.kuma.cloud.gateway.support.GatewayHeaders;
import org.apache.skywalking.apm.toolkit.trace.ContextCarrierRef;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.trace.Tracer;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/** Provides an async native entry span when the Agent has no plugin for this WebFlux version. */
public class GatewaySkyWalkingWebFilter implements WebFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            if (active(TraceContext.traceId())) {
                exchange.getAttributes().put(GatewayHeaders.SKYWALKING_SNAPSHOT_ATTRIBUTE, Tracer.capture());
                return chain.filter(exchange);
            }
            var carrier = new ContextCarrierRef();
            var item = carrier.items();
            while (item.hasNext()) {
                item = item.next();
                String value = exchange.getRequest().getHeaders().getFirst(item.getHeadKey());
                if (value != null) item.setHeadValue(value);
            }
            var span = Tracer.createEntrySpan("Gateway " + exchange.getRequest().getMethod().name(), carrier);
            try {
                span.tag("http.method", exchange.getRequest().getMethod().name());
                span.tag("http.url", exchange.getRequest().getURI().getPath());
                span.prepareForAsync();
                String traceId = TraceContext.traceId();
                if (active(traceId)) {
                    exchange.getAttributes().put(GatewayHeaders.SKYWALKING_ID_ATTRIBUTE, traceId);
                    exchange.getAttributes().put(GatewayHeaders.SKYWALKING_SNAPSHOT_ATTRIBUTE, Tracer.capture());
                }
            } finally {
                // Never retain an Agent ThreadLocal on a shared Reactor/Netty thread.
                Tracer.stopSpan();
            }
            return Mono.defer(() -> chain.filter(exchange))
                    .doOnError(error -> {
                        span.tag("error", "true");
                        span.log(error);
                    })
                    .doFinally(signal -> {
                        try {
                            var status = exchange.getResponse().getStatusCode();
                            if (status != null) span.tag("http.status_code", Integer.toString(status.value()));
                            if (status != null && status.is5xxServerError()) span.tag("error", "true");
                            if (signal == reactor.core.publisher.SignalType.CANCEL) span.tag("cancelled", "true");
                        } finally { span.asyncFinish(); }
                    });
        });
    }

    private static boolean active(String traceId) {
        return traceId != null && !traceId.isBlank() && !traceId.equals("N/A") && !traceId.equals("Ignored_Trace");
    }

    @Override
    public int getOrder() { return Ordered.HIGHEST_PRECEDENCE + 19; }
}
