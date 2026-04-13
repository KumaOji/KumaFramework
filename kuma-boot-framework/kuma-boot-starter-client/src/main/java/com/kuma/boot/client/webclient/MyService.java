package com.kuma.boot.client.webclient;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service("webClientMyService")
public class MyService {
   private final WebClient webClient;

   public MyService(WebClient.Builder webClientBuilder) {
      this.webClient = webClientBuilder.baseUrl("https://example.org").build();
   }

   public Mono<String> someRestCall(String name) {
      return this.webClient.get().uri("/{name}/details", new Object[]{name}).retrieve().bodyToMono(String.class);
   }
}
