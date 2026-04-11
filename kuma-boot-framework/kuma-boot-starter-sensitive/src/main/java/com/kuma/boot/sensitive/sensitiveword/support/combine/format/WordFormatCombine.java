package com.kuma.boot.sensitive.sensitiveword.support.combine.format;

import com.google.common.collect.Lists;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import com.kuma.boot.sensitive.sensitiveword.support.format.WordFormats;
import java.util.List;

public class WordFormatCombine extends AbstractWordFormatCombine {
   public WordFormatCombine() {
   }

   protected List<IWordFormat> getWordFormatList(IWordContext context) {
      List<IWordFormat> charFormats = Lists.newArrayList();
      if (context.ignoreEnglishStyle()) {
         charFormats.add(WordFormats.ignoreEnglishStyle());
      }

      if (context.ignoreCase()) {
         charFormats.add(WordFormats.ignoreCase());
      }

      if (context.ignoreWidth()) {
         charFormats.add(WordFormats.ignoreWidth());
      }

      if (context.ignoreNumStyle()) {
         charFormats.add(WordFormats.ignoreNumStyle());
      }

      if (context.ignoreChineseStyle()) {
         charFormats.add(WordFormats.ignoreChineseStyle());
      }

      return charFormats;
   }
}
