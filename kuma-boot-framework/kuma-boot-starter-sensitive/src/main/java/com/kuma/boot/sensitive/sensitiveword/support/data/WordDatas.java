package com.kuma.boot.sensitive.sensitiveword.support.data;

import com.kuma.boot.sensitive.sensitiveword.api.IWordData;

public final class WordDatas {
   private WordDatas() {
   }

   public static IWordData defaults() {
      return tree();
   }

   public static IWordData tree() {
      return new WordDataTree();
   }
}
