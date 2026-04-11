package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.support.pipeline.DefaultPipeline;
import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordLengthResult;

public abstract class WordCheckInit implements IWordCheck {
   public WordCheckInit() {
   }

   protected abstract void init(final Pipeline<IWordCheck> pipeline);

   public WordCheckResult sensitiveCheck(final int beginIndex, final InnerSensitiveWordContext checkContext) {
      Pipeline<IWordCheck> pipeline = new DefaultPipeline();
      this.init(pipeline);

      for(IWordCheck sensitiveCheck : pipeline.list()) {
         WordCheckResult result = sensitiveCheck.sensitiveCheck(beginIndex, checkContext);
         WordLengthResult wordLengthResult = result.wordLengthResult();
         if (wordLengthResult.wordAllowLen() > 0 || wordLengthResult.wordDenyLen() > 0) {
            return result;
         }
      }

      return WordCheckNone.getNoneResult();
   }
}
