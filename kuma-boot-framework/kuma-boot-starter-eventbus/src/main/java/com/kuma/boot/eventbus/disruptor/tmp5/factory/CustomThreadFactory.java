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
            LogUtils.error("\u7ebf\u7a0b\u5de5\u5382\u8bbe\u7f6eexceptionHandler\u6355\u83b7\u7ebf\u7a0b\u6267\u884c\u5f02\u5e38:{}", new Object[]{e.getMessage()});
            ThreadPoolExecutorErrorEvent threadPoolExecutorErrorEvent = new ThreadPoolExecutorErrorEvent(this, t1, e);
            ZlfDisruptorSpringUtils.getApplicationContext().publishEvent(threadPoolExecutorErrorEvent);
            LogUtils.info("====CustomFactory.\u6355\u83b7\u5f02\u5e38\u53d1\u9001springBoot\u4e8b\u4ef6\u5b8c\u6210======", new Object[0]);
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
