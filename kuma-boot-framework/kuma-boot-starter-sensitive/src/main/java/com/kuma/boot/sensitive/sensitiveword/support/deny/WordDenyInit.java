package com.kuma.boot.sensitive.sensitiveword.support.deny;

import com.kuma.boot.common.support.pipeline.DefaultPipeline;
import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.sensitive.sensitiveword.api.IWordDeny;
import java.util.ArrayList;
import java.util.List;

public abstract class WordDenyInit implements IWordDeny {
   public WordDenyInit() {
   }

   protected abstract void init(final Pipeline<IWordDeny> pipeline);

   public List<String> deny() {
      Pipeline<IWordDeny> pipeline = new DefaultPipeline();
      this.init(pipeline);
      List<String> results = new ArrayList();

      for(IWordDeny wordDeny : pipeline.list()) {
         List<String> denyList = wordDeny.deny();
         if (denyList == null) {
            denyList = new ArrayList();
         }

         results.addAll(denyList);
      }

      return results;
   }
}
