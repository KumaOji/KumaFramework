package com.kuma.boot.sensitive.sensitiveword.support.format;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;

public class WordFormatIgnoreCase implements IWordFormat {
   private static final IWordFormat INSTANCE = new WordFormatIgnoreCase();

   public WordFormatIgnoreCase() {
   }

   public static IWordFormat getInstance() {
      return INSTANCE;
   }

   public char format(char original, IWordContext context) {
      return Character.toLowerCase(original);
   }
}
