package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordCharIgnore;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordLengthResult;
import java.util.Map;

public abstract class AbstractConditionWordCheck extends AbstractWordCheck {
   public AbstractConditionWordCheck() {
   }

   protected abstract boolean isCharCondition(char mappingChar, int index, InnerSensitiveWordContext checkContext);

   protected abstract boolean isStringCondition(int index, final StringBuilder stringBuilder, InnerSensitiveWordContext checkContext);

   protected WordLengthResult getActualLength(int beginIndex, InnerSensitiveWordContext checkContext) {
      ISensitiveWordCharIgnore charIgnore = checkContext.wordContext().charIgnore();
      String txt = checkContext.originalText();
      char[] chars = txt.toCharArray();
      IWordContext context = checkContext.wordContext();
      Map<Character, Character> formatCharMapping = checkContext.formatCharMapping();
      int actualLength = 0;
      int tempIgnoreLen = 0;
      StringBuilder stringBuilder = new StringBuilder();
      int currentIx = 0;

      for(int i = beginIndex; i < txt.length(); ++i) {
         currentIx = i;
         boolean ignoreCharFlag = charIgnore.ignore(i, chars, checkContext);
         if (ignoreCharFlag) {
            ++tempIgnoreLen;
         } else {
            char currentChar = txt.charAt(i);
            char mappingChar = (Character)formatCharMapping.get(currentChar);
            boolean currentCondition = this.isCharCondition(mappingChar, i, checkContext);
            if (!currentCondition) {
               break;
            }

            stringBuilder.append(currentChar);
         }
      }

      if (this.isStringCondition(currentIx, stringBuilder, checkContext)) {
         actualLength = stringBuilder.length();
         actualLength += tempIgnoreLen;
      }

      tempIgnoreLen = 0;
      return WordLengthResult.newInstance().wordDenyLen(actualLength).wordAllowLen(0);
   }
}
