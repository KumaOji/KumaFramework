package com.kuma.boot.sensitive.sensitiveword.support.resultcondition;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;

public class WordResultConditionAlwaysTrue extends AbstractWordResultCondition {
   public WordResultConditionAlwaysTrue() {
   }

   protected boolean doMatch(IWordResult wordResult, String text, WordValidModeEnum modeEnum, IWordContext context) {
      return true;
   }
}
