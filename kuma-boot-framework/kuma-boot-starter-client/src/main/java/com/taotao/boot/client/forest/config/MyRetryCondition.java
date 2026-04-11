package com.taotao.boot.client.forest.config;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public class MyRetryCondition implements RetryWhen {
   public MyRetryCondition() {
   }

   public boolean retryWhen(ForestRequest req, ForestResponse res) {
      return res.statusIs(203);
   }
}
