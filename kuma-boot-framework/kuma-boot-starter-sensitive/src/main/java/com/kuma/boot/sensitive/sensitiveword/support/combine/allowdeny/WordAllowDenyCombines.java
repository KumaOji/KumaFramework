package com.kuma.boot.sensitive.sensitiveword.support.combine.allowdeny;

import com.kuma.boot.sensitive.sensitiveword.api.combine.IWordAllowDenyCombine;

public final class WordAllowDenyCombines {
   private WordAllowDenyCombines() {
   }

   public static IWordAllowDenyCombine defaults() {
      return new WordAllowDenyCombine();
   }
}
