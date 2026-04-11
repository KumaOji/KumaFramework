package com.kuma.boot.sensitive.sensitiveword.support.format;

import com.kuma.boot.common.utils.common.CharUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;

public class WordFormatIgnoreWidth implements IWordFormat {
   private static final IWordFormat INSTANCE = new WordFormatIgnoreWidth();

   public WordFormatIgnoreWidth() {
   }

   public static IWordFormat getInstance() {
      return INSTANCE;
   }

   public char format(char original, IWordContext context) {
      return CharUtils.toHalfWidth(original);
   }
}
