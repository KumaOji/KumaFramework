package com.kuma.boot.sensitive.sensitiveword.core;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWord;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultCondition;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;
import com.kuma.boot.sensitive.sensitiveword.support.check.WordCheckResult;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordResult;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordFormatUtils;
import java.util.List;
import java.util.Map;

public class SensitiveWord extends AbstractSensitiveWord {
   private static final ISensitiveWord INSTANCE = new SensitiveWord();

   public SensitiveWord() {
   }

   public static ISensitiveWord getInstance() {
      return INSTANCE;
   }

   protected List<IWordResult> doFindAll(String string, IWordContext context) {
      return this.innerSensitiveWords(string, WordValidModeEnum.FAIL_OVER, context);
   }

   protected IWordResult doFindFirst(String string, IWordContext context) {
      List<IWordResult> wordResults = this.innerSensitiveWords(string, WordValidModeEnum.FAIL_FAST, context);
      return !CollectionUtils.isEmpty(wordResults) ? (IWordResult)wordResults.get(0) : null;
   }

   private List<IWordResult> innerSensitiveWords(final String text, final WordValidModeEnum modeEnum, final IWordContext context) {
      IWordCheck sensitiveCheck = context.sensitiveCheck();
      List<IWordResult> resultList = Lists.newArrayList();
      Map<Character, Character> characterCharacterMap = InnerWordFormatUtils.formatCharsMapping(text, context);
      InnerSensitiveWordContext checkContext = InnerSensitiveWordContext.newInstance().originalText(text).wordContext(context).modeEnum(WordValidModeEnum.FAIL_OVER).formatCharMapping(characterCharacterMap);
      IWordResultCondition wordResultCondition = context.wordResultCondition();

      for(int i = 0; i < text.length(); ++i) {
         WordCheckResult checkResult = sensitiveCheck.sensitiveCheck(i, checkContext);
         int wordLengthAllow = checkResult.wordLengthResult().wordAllowLen();
         int wordLengthDeny = checkResult.wordLengthResult().wordDenyLen();
         if (wordLengthAllow < wordLengthDeny) {
            WordResult wordResult = WordResult.newInstance().startIndex(i).endIndex(i + wordLengthDeny).type(checkResult.type()).word(checkResult.wordLengthResult().wordDeny());
            if (wordResultCondition.match(wordResult, text, modeEnum, context)) {
               resultList.add(wordResult);
               if (WordValidModeEnum.FAIL_FAST.equals(modeEnum)) {
                  break;
               }
            }

            i += wordLengthDeny - 1;
         } else {
            i += Math.max(0, wordLengthAllow - 1);
         }
      }

      return resultList;
   }
}
