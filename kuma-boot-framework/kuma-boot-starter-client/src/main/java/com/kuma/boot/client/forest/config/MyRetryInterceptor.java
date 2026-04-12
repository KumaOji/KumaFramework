package com.kuma.boot.client.forest.config;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.kuma.boot.common.utils.log.LogUtils;

public class MyRetryInterceptor implements Interceptor {
   public MyRetryInterceptor() {
   }

   public void onRetry(ForestRequest req, ForestResponse res) {
      LogUtils.info("\u8981\u91cd\u8bd5\u4e86\uff01\u5f53\u524d\u91cd\u8bd5\u6b21\u6570\uff1a" + req.getCurrentRetryCount(), new Object[0]);
   }
}
