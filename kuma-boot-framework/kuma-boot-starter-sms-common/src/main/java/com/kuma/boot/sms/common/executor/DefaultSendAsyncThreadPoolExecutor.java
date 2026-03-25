package com.kuma.boot.sms.common.executor;

import com.kuma.boot.sms.common.properties.SmsAsyncProperties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultSendAsyncThreadPoolExecutor extends AbstractSendAsyncThreadPoolExecutor {
   private final ThreadPoolExecutor executor;

   public DefaultSendAsyncThreadPoolExecutor(SmsAsyncProperties properties) {
      super(properties);
      this.executor = new ThreadPoolExecutor(properties.getCorePoolSize(), properties.getMaximumPoolSize(), properties.getKeepAliveTime(), properties.getUnit(), new LinkedBlockingQueue(properties.getQueueCapacity()), new AbstractSendAsyncThreadPoolExecutor.DefaultThreadFactory(), buildRejectedExecutionHandler(properties.getRejectPolicy()));
   }

   protected void submit0(Runnable command) {
      this.executor.execute(command);
   }
}
