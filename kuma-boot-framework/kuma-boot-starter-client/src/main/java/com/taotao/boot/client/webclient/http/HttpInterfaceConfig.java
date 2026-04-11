package com.taotao.boot.client.webclient.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(
   basePackages = {"com.kuma.boot.*.api.inner"}
)
public class HttpInterfaceConfig {
   @Value("${remote.baseUrl}")
   private String baseUrl;
   private final TokenHolder tokenHolder;

   public HttpInterfaceConfig(TokenHolder tokenHolder) {
      this.tokenHolder = tokenHolder;
   }

   @Bean
   WebClient webClient() {
      return WebClient.builder().defaultHeader("source", new String[]{"http-interface"}).filter((request, next) -> {
         ClientRequest filtered = ClientRequest.from(request).header("Authorization", new String[]{this.tokenHolder.getToken()}).build();
         return next.exchange(filtered);
      }).baseUrl(this.baseUrl).build();
   }

   @Bean
   UmsAdminApi umsAdminApi(WebClient client) {
      HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
      return (UmsAdminApi)factory.createClient(UmsAdminApi.class);
   }

   @Bean
   PmsBrandApi pmsBrandApi(WebClient client) {
      HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(client)).build();
      return (PmsBrandApi)factory.createClient(PmsBrandApi.class);
   }
}
