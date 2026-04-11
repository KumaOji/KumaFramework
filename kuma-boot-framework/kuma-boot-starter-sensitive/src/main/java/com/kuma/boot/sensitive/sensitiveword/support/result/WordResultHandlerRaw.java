package com.kuma.boot.sensitive.sensitiveword.support.result;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;

public class WordResultHandlerRaw extends AbstractWordResultHandler<IWordResult> {
   private static final WordResultHandlerRaw INSTANCE = new WordResultHandlerRaw();

   public WordResultHandlerRaw() {
   }

   public static WordResultHandlerRaw getInstance() {
      return INSTANCE;
   }

   protected IWordResult doHandle(IWordResult wordResult, IWordContext wordContext, String originalText) {
      return wordResult;
   }
}
