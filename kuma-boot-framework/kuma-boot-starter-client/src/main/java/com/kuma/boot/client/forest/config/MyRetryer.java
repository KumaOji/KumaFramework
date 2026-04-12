package com.kuma.boot.client.forest.config;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.retryer.BackOffRetryer;

public class MyRetryer extends BackOffRetryer {
   public MyRetryer(ForestRequest request) {
      super(request);
   }

   protected long nextInterval(int currentCount) {
      return 1000L;
   }
}
