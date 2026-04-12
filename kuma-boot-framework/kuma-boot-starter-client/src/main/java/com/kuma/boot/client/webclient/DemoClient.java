package com.kuma.boot.client.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class DemoClient {
   public DemoClient() {
   }

   @Bean
   public DemoApi demoApi() {
      WebClient client = WebClient.builder().baseUrl("http://pigx.pigx.vip/").build();
      HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(client)).build();
      return (DemoApi)factory.createClient(DemoApi.class);
   }
}
