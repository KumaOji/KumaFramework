package com.kuma.boot.sensitive.sensitiveword.core;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWord;

public final class SensitiveWords {
   private SensitiveWords() {
   }

   public static ISensitiveWord defaults() {
      return SensitiveWord.getInstance();
   }
}
