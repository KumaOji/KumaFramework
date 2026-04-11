package com.kuma.boot.sensitive.sensitiveword.utils;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InnerWordFormatUtils {
   private static final char[] EMPTY_CHARS = new char[0];

   private InnerWordFormatUtils() {
   }

   public static String format(final String original, final IWordContext context) {
      if (StringUtils.isEmpty(original)) {
         return original;
      } else {
         StringBuilder stringBuilder = new StringBuilder();
         IWordFormat charFormat = context.wordFormat();
         char[] chars = original.toCharArray();

         for(char c : chars) {
            char cf = charFormat.format(c, context);
            stringBuilder.append(cf);
         }

         return stringBuilder.toString();
      }
   }

   public static Map<Character, Character> formatCharsMapping(final String original, final IWordContext context) {
      if (StringUtils.isEmpty(original)) {
         return Collections.emptyMap();
      } else {
         int len = original.length();
         char[] rawChars = original.toCharArray();
         Map<Character, Character> map = new HashMap(rawChars.length);
         IWordFormat charFormat = context.wordFormat();

         for(int i = 0; i < len; ++i) {
            char currentChar = rawChars[i];
            char formatChar = charFormat.format(currentChar, context);
            map.put(currentChar, formatChar);
         }

         return map;
      }
   }

   public static List<String> formatWordList(Collection<String> list, final IWordContext context) {
      if (CollectionUtils.isEmpty(list)) {
         return new ArrayList();
      } else {
         List<String> resultList = new ArrayList(list.size());

         for(String word : list) {
            String formatWord = format(word, context);
            resultList.add(formatWord);
         }

         return resultList;
      }
   }
}
