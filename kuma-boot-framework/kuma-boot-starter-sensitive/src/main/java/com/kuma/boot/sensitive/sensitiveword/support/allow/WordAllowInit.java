package com.kuma.boot.sensitive.sensitiveword.support.allow;

import com.kuma.boot.common.support.pipeline.DefaultPipeline;
import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.sensitive.sensitiveword.api.IWordAllow;
import java.util.ArrayList;
import java.util.List;

public abstract class WordAllowInit implements IWordAllow {
   public WordAllowInit() {
   }

   protected abstract void init(final Pipeline<IWordAllow> pipeline);

   public List<String> allow() {
      Pipeline<IWordAllow> pipeline = new DefaultPipeline();
      this.init(pipeline);
      List<String> results = new ArrayList();

      for(IWordAllow wordAllow : pipeline.list()) {
         List<String> allowList = wordAllow.allow();
         if (allowList == null) {
            allowList = new ArrayList();
         }

         results.addAll(allowList);
      }

      return results;
   }
}
