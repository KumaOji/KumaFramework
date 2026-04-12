package com.kuma.boot.client.forest.config;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public class MySuccessCondition implements SuccessWhen {
   public MySuccessCondition() {
   }

   public boolean successWhen(ForestRequest req, ForestResponse res) {
      return res.noException() && res.statusOk() && res.statusIsNot(203);
   }
}
