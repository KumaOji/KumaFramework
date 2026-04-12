package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;
import com.kuma.boot.ddd.gateway.model.GatewayResponseStatus;

public class ExceptionProcessInterceptor implements GatewayPostInterceptor {
   private static final ExceptionProcessInterceptor INSTANCE = new ExceptionProcessInterceptor();

   private ExceptionProcessInterceptor() {
   }

   public static ExceptionProcessInterceptor getInstance() {
      return INSTANCE;
   }

   public void intercept(GatewayResponse response, GatewayContext context) {
      LogUtils.error("Gateway traceId:{}, description:{}", new Object[]{context.getGatewayRecord().getTraceId(), context.getDescription(), context.getCatchedException()});
      if (context.isReachedRoute()) {
         response.setStatus(GatewayResponseStatus.P);
      } else {
         response.setStatus(GatewayResponseStatus.F);
         response.setFailCode("999999");
      }

   }

   public boolean shouldFilter(GatewayContext gatewayContext) {
      return gatewayContext.getCatchedException() != null;
   }
}
