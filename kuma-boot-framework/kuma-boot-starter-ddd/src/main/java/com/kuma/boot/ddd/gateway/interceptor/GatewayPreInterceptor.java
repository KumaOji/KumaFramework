//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;

public interface GatewayPreInterceptor<T> {
   void intercept(GatewayRequest<T> request, GatewayContext context);

   default boolean shouldFilter(GatewayContext context) {
      return true;
   }
}
