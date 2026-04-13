package com.kuma.boot.client.restclient;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service("restClientMyService")
public class MyService {
   private final RestClient restClient;

   public MyService(RestClient.Builder restClientBuilder) {
      this.restClient = restClientBuilder.baseUrl("https://example.org").build();
   }

   public String someRestCall(String name) {
      return (String)this.restClient.get().uri("/{name}/details", new Object[]{name}).retrieve().body(String.class);
   }
}
