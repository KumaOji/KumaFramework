package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;

public interface GatewayPostInterceptor {
   void intercept(GatewayResponse response, GatewayContext context);

   default boolean shouldFilter(GatewayContext context) {
      return true;
   }
}
