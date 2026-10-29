package com.kuma.boot.client.forest.config;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.kuma.boot.common.utils.log.LogUtils;

public class MyRetryInterceptor implements Interceptor {
   public MyRetryInterceptor() {
   }

   public void onRetry(ForestRequest req, ForestResponse res) {
      LogUtils.info("要重试了！当前重试次数：" + req.getCurrentRetryCount(), new Object[0]);
   }
}
