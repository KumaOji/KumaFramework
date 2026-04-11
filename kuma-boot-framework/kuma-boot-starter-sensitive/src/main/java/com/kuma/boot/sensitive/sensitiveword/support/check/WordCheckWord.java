package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordCharIgnore;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordData;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordContainsTypeEnum;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordTypeEnum;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordLengthResult;
import java.util.Map;

public class WordCheckWord extends AbstractWordCheck {
   private static final IWordCheck INSTANCE = new WordCheckWord();

   public WordCheckWord() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   protected Class<? extends IWordCheck> getSensitiveCheckClass() {
      return WordCheckWord.class;
   }

   protected WordLengthResult getActualLength(int beginIndex, InnerSensitiveWordContext innerContext) {
      String txt = innerContext.originalText();
      Map<Character, Character> formatCharMapping = innerContext.formatCharMapping();
      IWordContext context = innerContext.wordContext();
      IWordData wordData = context.wordData();
      IWordData wordDataAllow = context.wordDataAllow();
      ISensitiveWordCharIgnore wordCharIgnore = context.charIgnore();
      boolean failFast = context.wordFailFast();
      StringBuilder stringBuilder = new StringBuilder();
      char[] rawChars = txt.toCharArray();
      int tempLen = 0;
      int maxWhite = 0;
      int maxBlack = 0;
      int skipLen = 0;

      for(int i = beginIndex; i < rawChars.length; ++i) {
         if (wordCharIgnore.ignore(i, rawChars, innerContext) && tempLen != 0) {
            ++tempLen;
            ++skipLen;
         } else {
            char mappingChar = (Character)formatCharMapping.get(rawChars[i]);
            stringBuilder.append(mappingChar);
            ++tempLen;
            WordContainsTypeEnum wordContainsTypeEnumAllow = wordDataAllow.contains(stringBuilder, innerContext);
            WordContainsTypeEnum wordContainsTypeEnumDeny = wordData.contains(stringBuilder, innerContext);
            if (WordContainsTypeEnum.CONTAINS_END.equals(wordContainsTypeEnumAllow)) {
               maxWhite = tempLen;
               if (failFast) {
                  wordContainsTypeEnumAllow = WordContainsTypeEnum.NOT_FOUND;
               }
            }

            if (WordContainsTypeEnum.CONTAINS_END.equals(wordContainsTypeEnumDeny)) {
               maxBlack = tempLen;
               if (failFast) {
                  wordContainsTypeEnumDeny = WordContainsTypeEnum.NOT_FOUND;
               }
            }

            if (WordContainsTypeEnum.NOT_FOUND.equals(wordContainsTypeEnumAllow) && WordContainsTypeEnum.NOT_FOUND.equals(wordContainsTypeEnumDeny)) {
               break;
            }
         }
      }

      String string = stringBuilder.toString();
      String wordAllow = string.substring(0, Math.max(0, maxWhite - skipLen));
      String wordDeny = string.substring(0, Math.max(0, maxBlack - skipLen));
      return WordLengthResult.newInstance().wordAllowLen(maxWhite).wordDenyLen(maxBlack).wordAllow(wordAllow).wordDeny(wordDeny);
   }

   protected String getType() {
      return WordTypeEnum.WORD.getCode();
   }
}
