package com.kuma.boot.sensitive.sensitiveword.support.combine.check;

import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordCheckCombine;

public final class WordCheckCombines {
   private WordCheckCombines() {
   }

   public static IWordCheckCombine defaults() {
      return new WordCheckCombine();
   }
}
