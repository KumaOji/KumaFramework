package com.kuma.boot.sensitive.sensitiveword.support.resultcondition;

import com.kuma.boot.common.utils.lang.CharUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;

public class WordResultConditionEnglishWordMatch extends AbstractWordResultCondition {
   public WordResultConditionEnglishWordMatch() {
   }

   protected boolean doMatch(IWordResult wordResult, String text, WordValidModeEnum modeEnum, IWordContext context) {
      int startIndex = wordResult.startIndex();
      int endIndex = wordResult.endIndex();
      if (startIndex > 0) {
         char preC = text.charAt(startIndex - 1);
         if (CharUtils.isEnglish(preC)) {
            return false;
         }
      }

      if (endIndex < text.length()) {
         char afterC = text.charAt(endIndex);
         if (CharUtils.isEnglish(afterC)) {
            return false;
         }
      }

      for(int i = startIndex; i < endIndex; ++i) {
         char c = text.charAt(i);
         if (!CharUtils.isEnglish(c)) {
            return true;
         }
      }

      return true;
   }
}
