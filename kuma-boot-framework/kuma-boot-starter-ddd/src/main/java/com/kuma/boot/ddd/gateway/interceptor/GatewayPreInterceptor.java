package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;

public interface GatewayPreInterceptor {
   void intercept(GatewayRequest request, GatewayContext context);

   default boolean shouldFilter(GatewayContext context) {
      return true;
   }
}
