package com.kuma.boot.sms.common.executor;

public interface SendAsyncThreadPoolExecutor {
   void submit(Runnable command);
}
