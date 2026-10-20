package com.kuma.cloud.gateway.filter;

import com.kuma.cloud.gateway.support.GatewayHeaders;
import org.apache.skywalking.apm.toolkit.trace.ContextCarrierRef;
import org.apache.skywalking.apm.toolkit.trace.ContextSnapshotRef;
import org.apache.skywalking.apm.toolkit.trace.Tracer;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/** Injects the native SkyWalking carrier after a gateway route has been selected. */
public class GatewaySkyWalkingRelayGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.defer(() -> {
            ContextSnapshotRef snapshot = exchange.getAttribute(GatewayHeaders.SKYWALKING_SNAPSHOT_ATTRIBUTE);
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            if (snapshot == null || route == null) return chain.filter(exchange);
            var carrier = new ContextCarrierRef();
            var span = Tracer.createExitSpan("Gateway route " + route.getId(), route.getUri().getAuthority());
            ServerWebExchange forwarded;
            try {
                Tracer.continued(snapshot);
                Tracer.inject(carrier);
                var request = exchange.getRequest().mutate();
                var item = carrier.items();
                while (item.hasNext()) {
                    item = item.next();
                    String key = item.getHeadKey();
                    String value = item.getHeadValue();
                    request.headers(headers -> headers.set(key, value));
                }
                forwarded = exchange.mutate().request(request.build()).build();
                span.prepareForAsync();
            } finally { Tracer.stopSpan(); }
            return Mono.defer(() -> chain.filter(forwarded)).doOnError(span::log)
                    .doFinally(signal -> span.asyncFinish());
        });
    }

    @Override
    public int getOrder() { return -1; }
}
