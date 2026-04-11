package com.kuma.boot.sensitive.sensitiveword.support.resultcondition;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultCondition;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;

public abstract class AbstractWordResultCondition implements IWordResultCondition {
   public AbstractWordResultCondition() {
   }

   protected abstract boolean doMatch(IWordResult wordResult, String text, WordValidModeEnum modeEnum, IWordContext context);

   public boolean match(IWordResult wordResult, String text, WordValidModeEnum modeEnum, IWordContext context) {
      return this.doMatch(wordResult, text, modeEnum, context);
   }
}
