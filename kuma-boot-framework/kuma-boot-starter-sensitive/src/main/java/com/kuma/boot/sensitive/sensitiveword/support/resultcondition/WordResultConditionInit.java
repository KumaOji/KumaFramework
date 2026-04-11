package com.kuma.boot.sensitive.sensitiveword.support.resultcondition;

import com.kuma.boot.common.support.pipeline.DefaultPipeline;
import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultCondition;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;

public abstract class WordResultConditionInit extends AbstractWordResultCondition {
   public WordResultConditionInit() {
   }

   protected abstract void init(final Pipeline<IWordResultCondition> pipeline);

   protected boolean doMatch(IWordResult wordResult, String text, WordValidModeEnum modeEnum, IWordContext context) {
      Pipeline<IWordResultCondition> pipeline = new DefaultPipeline();
      this.init(pipeline);

      for(IWordResultCondition wordResultCondition : pipeline.list()) {
         if (!wordResultCondition.match(wordResult, text, modeEnum, context)) {
            return false;
         }
      }

      return true;
   }
}
