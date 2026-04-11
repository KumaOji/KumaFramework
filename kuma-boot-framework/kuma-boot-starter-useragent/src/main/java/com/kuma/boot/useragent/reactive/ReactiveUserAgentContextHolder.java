package com.kuma.boot.useragent.reactive;

import com.kuma.boot.useragent.domain.UserAgent;
import java.util.function.Function;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveUserAgentContextHolder {
   private static final Class<?> USER_AGENT_KEY = ReactiveUserAgentContextHolder.class;

   public ReactiveUserAgentContextHolder() {
   }

   public static Mono<UserAgent> get() {
      return Mono.deferContextual(Mono::just).cast(Context.class).filter(ReactiveUserAgentContextHolder::hasContext).flatMap(ReactiveUserAgentContextHolder::getContext);
   }

   private static boolean hasContext(Context context) {
      return context.hasKey(USER_AGENT_KEY);
   }

   private static Mono<UserAgent> getContext(Context context) {
      return (Mono)context.get(USER_AGENT_KEY);
   }

   public static Function<Context, Context> clearContext() {
      return (context) -> context.delete(USER_AGENT_KEY);
   }

   public static Context withContext(Mono<? extends UserAgent> useragentWrapper) {
      return Context.of(USER_AGENT_KEY, useragentWrapper);
   }

   public static Context withContext(UserAgent userAgent) {
      return withContext(Mono.just(userAgent));
   }
}
