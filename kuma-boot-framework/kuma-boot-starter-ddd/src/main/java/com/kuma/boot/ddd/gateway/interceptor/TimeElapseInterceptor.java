package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;

public class TimeElapseInterceptor implements GatewayPreInterceptor, GatewayPostInterceptor {
   private static final String START_TIME_MARK = "START_TIME_MARK";
   private static final TimeElapseInterceptor INSTANCE = new TimeElapseInterceptor();

   private TimeElapseInterceptor() {
   }

   public static TimeElapseInterceptor getInstance() {
      return INSTANCE;
   }

   public boolean shouldFilter(GatewayContext context) {
      return true;
   }

   public void intercept(GatewayRequest request, GatewayContext context) {
      context.getExtraInfo().put("START_TIME_MARK", System.currentTimeMillis());
   }

   public void intercept(GatewayResponse response, GatewayContext context) {
      long endTime = System.currentTimeMillis();
      long startTime = (Long)context.getExtraInfo().get("START_TIME_MARK");
      LogUtils.info("Gateway description:{}, cost:{} ms", new Object[]{context.getDescription(), endTime - startTime});
   }
}
