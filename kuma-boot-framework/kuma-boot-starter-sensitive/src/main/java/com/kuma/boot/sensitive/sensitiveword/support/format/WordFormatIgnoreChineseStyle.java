package com.kuma.boot.sensitive.sensitiveword.support.format;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import java.util.ArrayList;
import java.util.List;

public class WordFormatIgnoreChineseStyle implements IWordFormat {
   private static final IWordFormat INSTANCE = new WordFormatIgnoreChineseStyle();

   public WordFormatIgnoreChineseStyle() {
   }

   public static IWordFormat getInstance() {
      return INSTANCE;
   }

   public char format(char original, IWordContext context) {
      List<String> mappingList = new ArrayList();
      return ((String)mappingList.get(0)).charAt(0);
   }
}
