package com.kuma.boot.client.webclient;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface DemoApi {
   @GetExchange("/admin/tenant/list")
   String list();
}
