package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordLengthResult;

public abstract class AbstractWordCheck implements IWordCheck {
   public AbstractWordCheck() {
   }

   protected abstract Class<? extends IWordCheck> getSensitiveCheckClass();

   protected abstract WordLengthResult getActualLength(int beginIndex, final InnerSensitiveWordContext checkContext);

   protected abstract String getType();

   public WordCheckResult sensitiveCheck(int beginIndex, final InnerSensitiveWordContext checkContext) {
      Class<? extends IWordCheck> clazz = this.getSensitiveCheckClass();
      String txt = checkContext.originalText();
      WordLengthResult wordLengthResult = WordLengthResult.newInstance().wordAllowLen(0).wordDenyLen(0);
      if (StringUtils.isEmpty(txt)) {
         return WordCheckResult.newInstance().wordLengthResult(wordLengthResult).type(this.getType()).checkClass(clazz);
      } else {
         wordLengthResult = this.getActualLength(beginIndex, checkContext);
         return WordCheckResult.newInstance().wordLengthResult(wordLengthResult).type(this.getType()).checkClass(clazz);
      }
   }
}
