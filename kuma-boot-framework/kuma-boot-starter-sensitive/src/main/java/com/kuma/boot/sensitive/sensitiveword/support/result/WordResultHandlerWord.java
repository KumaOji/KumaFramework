package com.kuma.boot.sensitive.sensitiveword.support.result;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordCharUtils;

public class WordResultHandlerWord extends AbstractWordResultHandler<String> {
   private static final WordResultHandlerWord INSTANCE = new WordResultHandlerWord();

   public WordResultHandlerWord() {
   }

   public static WordResultHandlerWord getInstance() {
      return INSTANCE;
   }

   protected String doHandle(IWordResult wordResult, IWordContext wordContext, String originalText) {
      return InnerWordCharUtils.getString(originalText.toCharArray(), wordResult);
   }
}
