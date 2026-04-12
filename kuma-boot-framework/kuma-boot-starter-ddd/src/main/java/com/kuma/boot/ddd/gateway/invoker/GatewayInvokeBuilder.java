package com.kuma.boot.ddd.gateway.invoker;

import com.kuma.boot.ddd.gateway.interceptor.GatewayPostInterceptor;
import com.kuma.boot.ddd.gateway.interceptor.GatewayPreInterceptor;
import com.kuma.boot.ddd.gateway.model.GatewayRouter;
import java.util.LinkedList;

public class GatewayInvokeBuilder {
   private final LinkedList preInterceptors = new LinkedList();
   private final LinkedList postInterceptors = new LinkedList();
   private GatewayRouter gatewayRouter;
   private String description;

   public GatewayInvokeBuilder description(String description) {
      this.description = description;
      return this;
   }

   public GatewayInvokeBuilder gatewayRouter(GatewayRouter router) {
      this.gatewayRouter = router;
      return this;
   }

   public GatewayInvokeBuilder addFirst(GatewayPreInterceptor interceptor) {
      this.preInterceptors.addFirst(interceptor);
      return this;
   }

   public GatewayInvokeBuilder addLast(GatewayPreInterceptor interceptor) {
      this.preInterceptors.addLast(interceptor);
      return this;
   }

   public GatewayInvokeBuilder addFirst(GatewayPostInterceptor interceptor) {
      this.postInterceptors.addFirst(interceptor);
      return this;
   }

   public GatewayInvokeBuilder addLast(GatewayPostInterceptor interceptor) {
      this.postInterceptors.addLast(interceptor);
      return this;
   }

   public GatewayInvokeTemplate build() {
      return new GatewayInvokeTemplate(this.preInterceptors, this.postInterceptors, this.gatewayRouter, this.description);
   }
}
