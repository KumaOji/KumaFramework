package com.kuma.boot.useragent.reactive;

import com.kuma.boot.useragent.annotation.UserAgentInfo;
import com.kuma.boot.useragent.domain.UserAgent;
import com.kuma.boot.useragent.helper.UserAgentHelper;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveUserAgentResolver implements HandlerMethodArgumentResolver {
   private final ReactiveAdapterRegistry adapterRegistry = ReactiveAdapterRegistry.getSharedInstance();

   public ReactiveUserAgentResolver() {
   }

   public final boolean supportsParameter(MethodParameter parameter) {
      return parameter.hasParameterAnnotation(UserAgentInfo.class);
   }

   public final @NonNull Mono<Object> resolveArgument(@NonNull MethodParameter parameter, @NonNull BindingContext bindingContext, ServerWebExchange exchange) {
      Class<?> resolvedType = ResolvableType.forMethodParameter(parameter).resolve();
      ReactiveAdapter adapter = resolvedType != null ? this.adapterRegistry.getAdapter(resolvedType) : null;
      Mono<UserAgent> mono = ReactiveUserAgentContextHolder.get().switchIfEmpty(Mono.justOrEmpty(UserAgentHelper.convert(exchange.getRequest().getHeaders())));
      return adapter != null ? Mono.just(adapter.fromPublisher(mono)) : Mono.from(mono);
   }
}
