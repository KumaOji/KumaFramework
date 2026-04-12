package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;
import org.apache.commons.lang3.StringUtils;

public class LogInterceptor implements GatewayPreInterceptor, GatewayPostInterceptor {
   private static final LogInterceptor INSTANCE = new LogInterceptor();

   private LogInterceptor() {
   }

   public static LogInterceptor getInstance() {
      return INSTANCE;
   }

   public void intercept(GatewayResponse response, GatewayContext context) {
      String substring = "";
      if (context.getRawResponse() != null) {
         String json = JacksonUtils.toJson(context.getRawResponse());
         if (json != null) {
            substring = this.substring(json);
         }
      }

      LogUtils.info("Gateway description:{}, output:{}", new Object[]{context.getDescription(), substring});
   }

   public void intercept(GatewayRequest request, GatewayContext context) {
      LogUtils.info("Gateway description:{}, input:{}", new Object[]{context.getDescription(), JacksonUtils.toJson(request.getParam())});
   }

   private String substring(String str) {
      return str.length() > 200 ? StringUtils.substring(str, 0, 4000) + "...." : str;
   }

   public boolean shouldFilter(GatewayContext context) {
      return true;
   }
}
