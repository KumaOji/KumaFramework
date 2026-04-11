package com.taotao.boot.client.webclient;

import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Flux;

public interface TenantClient {
   @GetExchange("/tenants")
   Flux<String> getAll();
}
