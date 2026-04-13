//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

public class GatewayInvokeTemplate<P, R> {
   private LinkedList<GatewayPreInterceptor<P>> preInterceptors;
   private LinkedList<GatewayPostInterceptor<R>> postInterceptors;
   private GatewayRouter<P> gatewayRouter;
   private String description;

   public GatewayInvokeTemplate(LinkedList<GatewayPreInterceptor<P>> preInterceptors, LinkedList<GatewayPostInterceptor<R>> postInterceptors, GatewayRouter<P> gatewayRouter, String description) {
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

   public GatewayInvokeTemplate(LinkedList<GatewayPreInterceptor<P>> preInterceptors, LinkedList<GatewayPostInterceptor<R>> postInterceptors, GatewayRouter<P> gatewayRouter, String description, Object ext) {
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

   public GatewayResponse<R> invoke(GatewayRequest<P> request) {
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

   private void doPre(GatewayRequest<P> request, GatewayContext context) {
      for(GatewayPreInterceptor<P> interceptor : this.preInterceptors) {
         interceptor.intercept(request, context);
      }

   }

   private void doPost(GatewayResponse<R> response, GatewayContext context) {
      for(GatewayPostInterceptor<R> interceptor : this.postInterceptors) {
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

   public LinkedList<GatewayPreInterceptor<P>> getPreInterceptors() {
      return this.preInterceptors;
   }

   public void setPreInterceptors(LinkedList<GatewayPreInterceptor<P>> preInterceptors) {
      this.preInterceptors = preInterceptors;
   }

   public LinkedList<GatewayPostInterceptor<R>> getPostInterceptors() {
      return this.postInterceptors;
   }

   public void setPostInterceptors(LinkedList<GatewayPostInterceptor<R>> postInterceptors) {
      this.postInterceptors = postInterceptors;
   }

   public GatewayRouter<P> getGatewayRouter() {
      return this.gatewayRouter;
   }

   public void setGatewayRouter(GatewayRouter<P> gatewayRouter) {
      this.gatewayRouter = gatewayRouter;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
