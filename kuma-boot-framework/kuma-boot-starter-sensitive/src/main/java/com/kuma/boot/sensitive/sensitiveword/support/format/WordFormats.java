package com.kuma.boot.sensitive.sensitiveword.support.format;

import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.util.Collection;

public final class WordFormats {
   private WordFormats() {
   }

   public static IWordFormat chains(final IWordFormat... charFormats) {
      return (IWordFormat)(ArrayUtils.isEmpty(charFormats) ? none() : new WordFormatInit() {
         protected void init(Pipeline<IWordFormat> pipeline) {
            for(IWordFormat charFormat : charFormats) {
               pipeline.addLast(charFormat);
            }

         }
      });
   }

   public static IWordFormat chains(final Collection<IWordFormat> charFormats) {
      return (IWordFormat)(CollectionUtil.isEmpty(charFormats) ? none() : new WordFormatInit() {
         protected void init(Pipeline<IWordFormat> pipeline) {
            for(IWordFormat charFormat : charFormats) {
               pipeline.addLast(charFormat);
            }

         }
      });
   }

   public static IWordFormat none() {
      return WordFormatNone.getInstance();
   }

   public static IWordFormat ignoreCase() {
      return WordFormatIgnoreCase.getInstance();
   }

   public static IWordFormat ignoreEnglishStyle() {
      return WordFormatIgnoreEnglishStyle.getInstance();
   }

   public static IWordFormat ignoreChineseStyle() {
      return WordFormatIgnoreChineseStyle.getInstance();
   }

   public static IWordFormat ignoreNumStyle() {
      return WordFormatIgnoreNumStyle.getInstance();
   }

   public static IWordFormat ignoreWidth() {
      return WordFormatIgnoreWidth.getInstance();
   }
}
