package com.kuma.boot.sensitive.sensitiveword.support.replace;

import com.kuma.boot.sensitive.sensitiveword.api.IWordReplace;

public final class WordReplaces {
   private WordReplaces() {
   }

   public static IWordReplace chars(final char c) {
      return new WordReplaceChar(c);
   }

   public static IWordReplace chars() {
      return new WordReplaceChar();
   }

   public static IWordReplace defaults() {
      return chars();
   }
}
