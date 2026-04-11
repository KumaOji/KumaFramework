package com.kuma.boot.sensitive.sensitiveword.support.result;

import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultHandler;

public final class WordResultHandlers {
   private WordResultHandlers() {
   }

   public static IWordResultHandler<IWordResult> raw() {
      return WordResultHandlerRaw.getInstance();
   }

   public static IWordResultHandler<String> word() {
      return WordResultHandlerWord.getInstance();
   }

   public static IWordResultHandler<WordTagsDto> wordTags() {
      return new WordResultHandlerWordTags();
   }
}
