package com.kuma.boot.ddd.gateway.invoker;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ddd.gateway.interceptor.ExceptionProcessInterceptor;
import com.kuma.boot.ddd.gateway.interceptor.GatewayPostInterceptor;
import com.kuma.boot.ddd.gateway.interceptor.GatewayPreInterceptor;
import com.kuma.boot.ddd.gateway.interceptor.LogExtInterceptor;
import com.kuma.boot.ddd.gateway.interceptor.LogInterceptor;
import com.kuma.boot.ddd.gateway.interceptor.TimeElapseInterceptor;
import com.kuma.boot.ddd.gateway.model.GatewayContext;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;
import com.kuma.boot.ddd.gateway.model.GatewayRouter;
import java.util.LinkedList;

public class GatewayInvokeTemplate {
   private LinkedList preInterceptors;
   private LinkedList postInterceptors;
   private GatewayRouter gatewayRouter;
   private String description;

   public GatewayInvokeTemplate(LinkedList preInterceptors, LinkedList postInterceptors, GatewayRouter gatewayRouter, String description) {
      this.preInterceptors = preInterceptors;
      this.postInterceptors = postInterceptors;
      this.gatewayRouter = gatewayRouter;
      this.description = description;
      this.preInterceptors.addLast(LogInterceptor.getInstance());
      this.preInterceptors.addLast(TimeElapseInterceptor.getInstance());
      this.postInterceptors.addFirst(LogInterceptor.getInstance());
      this.postInterceptors.addFirst(TimeElapseInterceptor.getInstance());
      this.postInterceptors.addLast(ExceptionProcessInterceptor.getInstance());
   }

   public GatewayInvokeTemplate(LinkedList preInterceptors, LinkedList postInterceptors, GatewayRouter gatewayRouter, String description, Object ext) {
      this.preInterceptors = preInterceptors;
      this.postInterceptors = postInterceptors;
      this.gatewayRouter = gatewayRouter;
      this.description = description;
      this.preInterceptors.addLast(LogExtInterceptor.getInstance());
      this.preInterceptors.addLast(TimeElapseInterceptor.getInstance());
      this.postInterceptors.addFirst(LogExtInterceptor.getInstance());
      this.postInterceptors.addFirst(TimeElapseInterceptor.getInstance());
      this.postInterceptors.addLast(ExceptionProcessInterceptor.getInstance());
   }

   public GatewayResponse invoke(GatewayRequest request) {
      GatewayContext context = new GatewayContext();
      context.setDescription(this.description);
      context.setTraceId(request.getGatewayRecord().getTraceId());
      context.setGatewayRecord(request.getGatewayRecord());
      context.setTradeNo(request.getGatewayRecord().getTradeNo());
      context.setRequest(request.getParam());
      GatewayResponse<R> responseHolder = new GatewayResponse();
      responseHolder.setGatewayRecord(request.getGatewayRecord());

      try {
         this.doPre(request, context);
      } catch (Exception e) {
         context.setCatchedException(e);
         context.setReachedRoute(false);
         this.doPost(responseHolder, context);
         return responseHolder;
      }

      context.setReachedRoute(true);

      try {
         Object result = this.gatewayRouter.execute(request.getParam());
         context.setRawResponse(result);
      } catch (Exception e) {
         context.setCatchedException(e);
      } finally {
         this.doPost(responseHolder, context);
         return responseHolder;
      }
   }

   private void doPre(GatewayRequest request, GatewayContext context) {
      for(GatewayPreInterceptor interceptor : this.preInterceptors) {
         interceptor.intercept(request, context);
      }

   }

   private void doPost(GatewayResponse response, GatewayContext context) {
      for(GatewayPostInterceptor interceptor : this.postInterceptors) {
         try {
            if (interceptor.shouldFilter(context)) {
               interceptor.intercept(response, context);
            }
         } catch (Exception e) {
            if (context.getCatchedException() == null) {
               context.setCatchedException(e);
            } else {
               LogUtils.error("Duplicate catch exception", new Object[]{e});
            }
         }
      }

   }

   public LinkedList getPreInterceptors() {
      return this.preInterceptors;
   }

   public void setPreInterceptors(LinkedList preInterceptors) {
      this.preInterceptors = preInterceptors;
   }

   public LinkedList getPostInterceptors() {
      return this.postInterceptors;
   }

   public void setPostInterceptors(LinkedList postInterceptors) {
      this.postInterceptors = postInterceptors;
   }

   public GatewayRouter getGatewayRouter() {
      return this.gatewayRouter;
   }

   public void setGatewayRouter(GatewayRouter gatewayRouter) {
      this.gatewayRouter = gatewayRouter;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
