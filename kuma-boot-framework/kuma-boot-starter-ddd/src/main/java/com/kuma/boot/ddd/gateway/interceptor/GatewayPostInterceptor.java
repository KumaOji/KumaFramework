//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;

public interface GatewayPostInterceptor<T> {
   void intercept(GatewayResponse<T> response, GatewayContext context);

   default boolean shouldFilter(GatewayContext context) {
      return true;
   }
}
