package com.kuma.boot.sensitive.sensitiveword.support.format;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordCharUtils;

public class WordFormatIgnoreEnglishStyle implements IWordFormat {
   private static final IWordFormat INSTANCE = new WordFormatIgnoreEnglishStyle();

   public WordFormatIgnoreEnglishStyle() {
   }

   public static IWordFormat getInstance() {
      return INSTANCE;
   }

   public char format(char original, IWordContext context) {
      return InnerWordCharUtils.getMappingChar(original);
   }
}
