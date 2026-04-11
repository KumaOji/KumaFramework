package com.kuma.boot.useragent.reactive;

import com.kuma.boot.useragent.helper.UserAgentHelper;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class ReactiveUserAgentFilter implements WebFilter {
   public ReactiveUserAgentFilter() {
   }

   public final @NonNull Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      exchange.getResponse().beforeCommit(() -> Mono.deferContextual(Mono::just).contextWrite(ReactiveUserAgentContextHolder.clearContext()).then());
      HttpHeaders headers = exchange.getRequest().getHeaders();
      return chain.filter(exchange).contextWrite(ReactiveUserAgentContextHolder.withContext(UserAgentHelper.convert(headers)));
   }
}
