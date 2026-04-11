package com.kuma.boot.eventbus.disruptor.tmp5.handler;

import com.alibaba.fastjson2.JSON;
import com.lmax.disruptor.ExceptionHandler;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.event.CustomExceptionHandlerEvent;
import com.kuma.boot.eventbus.disruptor.tmp5.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp5.utils.ZlfDisruptorSpringUtils;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionHandler implements ExceptionHandler<DisruptorEvent> {
   @Autowired
   private ApplicationContext applicationContext;

   public CustomExceptionHandler() {
   }

   public void handleEventException(Throwable ex, long sequence, DisruptorEvent event) {
      LogUtils.info("CustomExceptionHandler.handleEventException===>sequence:{},event:{},ex:{}", new Object[]{sequence, JSON.toJSONString(event), ex});
      CustomExceptionHandlerEvent exceptionHandlerEvent = new CustomExceptionHandlerEvent(this, ex, sequence, event);
      if (Objects.isNull(this.applicationContext)) {
         this.applicationContext = ZlfDisruptorSpringUtils.getApplicationContext();
      }

      this.applicationContext.publishEvent(exceptionHandlerEvent);
      LogUtils.info("======CustomExceptionHandler\u53d1\u9001exceptionHandlerEvent:{}\u5b8c\u6210======", new Object[]{JSON.toJSONString(exceptionHandlerEvent)});
   }

   public void handleOnStartException(Throwable ex) {
      LogUtils.info("CustomExceptionHandler.handleOnStartException===>ex:{}", new Object[]{ex});
   }

   public void handleOnShutdownException(Throwable ex) {
      LogUtils.info("CustomExceptionHandler.handleOnShutdownException===>ex:{}", new Object[]{ex});
   }
}
