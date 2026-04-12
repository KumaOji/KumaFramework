package com.kuma.boot.ddd.gateway.interceptor;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class LogExtInterceptor implements GatewayPreInterceptor, GatewayPostInterceptor {
   private static final LogExtInterceptor INSTANCE = new LogExtInterceptor();

   private LogExtInterceptor() {
   }

   public static LogExtInterceptor getInstance() {
      return INSTANCE;
   }

   public void intercept(GatewayRequest request, GatewayContext context) {
      LogUtils.info("Gateway description:{}, input:{}", new Object[]{context.getDescription(), request.getParam()});
   }

   public void intercept(GatewayResponse response, GatewayContext context) {
      LogUtils.info("Gateway description:{}, output:{}", new Object[]{context.getDescription(), this.substring((String)Objects.requireNonNull(JacksonUtils.toJson(context.getRawResponse())))});
   }

   private String substring(String str) {
      return str.length() > 200 ? StringUtils.substring(str, 0, 4000) + "...." : str;
   }

   public boolean shouldFilter(GatewayContext context) {
      return true;
   }
}
