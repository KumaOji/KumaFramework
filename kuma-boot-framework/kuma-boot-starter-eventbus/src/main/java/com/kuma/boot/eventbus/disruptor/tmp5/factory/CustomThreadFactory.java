package com.kuma.boot.eventbus.disruptor.tmp5.factory;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.builder.CustomThreadBuilder;
import com.kuma.boot.eventbus.disruptor.tmp5.event.ThreadPoolExecutorErrorEvent;
import com.kuma.boot.eventbus.disruptor.tmp5.utils.ZlfDisruptorSpringUtils;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {
   private CustomThreadBuilder customThreadBuilder;

   public CustomThreadFactory(CustomThreadBuilder customThreadBuilder) {
      this.customThreadBuilder = customThreadBuilder;
   }

   public Thread newThread(Runnable r) {
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.customThreadBuilder.getUncaughtExceptionHandler();
      Thread t = new Thread(r, this.customThreadBuilder.getName());
      if (Objects.isNull(uncaughtExceptionHandler)) {
         t.setUncaughtExceptionHandler((t1, e) -> {
            e.printStackTrace();
            LogUtils.error("线程工厂设置exceptionHandler捕获线程执行异常:{}", new Object[]{e.getMessage()});
            ThreadPoolExecutorErrorEvent threadPoolExecutorErrorEvent = new ThreadPoolExecutorErrorEvent(this, t1, e);
            ZlfDisruptorSpringUtils.getApplicationContext().publishEvent(threadPoolExecutorErrorEvent);
            LogUtils.info("====CustomFactory.捕获异常发送springBoot事件完成======", new Object[0]);
         });
      } else {
         t.setUncaughtExceptionHandler(uncaughtExceptionHandler);
      }

      if (this.customThreadBuilder.getDaemon()) {
         t.setDaemon(true);
      }

      return t;
   }
}
