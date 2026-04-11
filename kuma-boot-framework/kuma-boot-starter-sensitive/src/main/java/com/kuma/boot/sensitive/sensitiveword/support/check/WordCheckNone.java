package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordTypeEnum;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordLengthResult;

public class WordCheckNone implements IWordCheck {
   private static final IWordCheck INSTANCE = new WordCheckNone();
   private static final WordCheckResult NONE_RESULT;

   public WordCheckNone() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   public static WordCheckResult getNoneResult() {
      return NONE_RESULT;
   }

   public WordCheckResult sensitiveCheck(int beginIndex, InnerSensitiveWordContext context) {
      return NONE_RESULT;
   }

   static {
      NONE_RESULT = WordCheckResult.newInstance().type(WordTypeEnum.DEFAULTS.getCode()).wordLengthResult(WordLengthResult.newInstance()).checkClass(WordCheckNone.class);
   }
}
