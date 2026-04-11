package com.kuma.boot.sensitive.sensitiveword.utils;

import cn.hutool.core.map.MapUtil;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import java.util.Map;

public final class InnerWordCharUtils {
   private static final String LETTERS_ONE = "\u24b6\u24b7\u24b8\u24b9\u24ba\u24bb\u24bc\u24bd\u24be\u24bf\u24c0\u24c1\u24c2\u24c3\u24c4\u24c5\u24c6\u24c7\u24c8\u24c9\u24ca\u24cb\u24cc\u24cd\u24ce\u24cf\u24d0\u24d1\u24d2\u24d3\u24d4\u24d5\u24d6\u24d7\u24d8\u24d9\u24da\u24db\u24dc\u24dd\u24de\u24df\u24e0\u24e1\u24e2\u24e3\u24e4\u24e5\u24e6\u24e7\u24e8\u24e9\u249c\u249d\u249e\u249f\u24a0\u24a1\u24a2\u24a3\u24a4\u24a5\u24a6\u24a7\u24a8\u24a9\u24aa\u24ab\u24ac\u24ad\u24ae\u24af\u24b0\u24b1\u24b2\u24b3\u24b4\u24b5";
   private static final String LETTERS_TWO = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
   private static final Map<Character, Character> LETTER_MAP = MapUtil.newHashMap("\u24b6\u24b7\u24b8\u24b9\u24ba\u24bb\u24bc\u24bd\u24be\u24bf\u24c0\u24c1\u24c2\u24c3\u24c4\u24c5\u24c6\u24c7\u24c8\u24c9\u24ca\u24cb\u24cc\u24cd\u24ce\u24cf\u24d0\u24d1\u24d2\u24d3\u24d4\u24d5\u24d6\u24d7\u24d8\u24d9\u24da\u24db\u24dc\u24dd\u24de\u24df\u24e0\u24e1\u24e2\u24e3\u24e4\u24e5\u24e6\u24e7\u24e8\u24e9\u249c\u249d\u249e\u249f\u24a0\u24a1\u24a2\u24a3\u24a4\u24a5\u24a6\u24a7\u24a8\u24a9\u24aa\u24ab\u24ac\u24ad\u24ae\u24af\u24b0\u24b1\u24b2\u24b3\u24b4\u24b5".length());

   private InnerWordCharUtils() {
   }

   public static Character getMappingChar(final Character character) {
      Character mapChar = (Character)LETTER_MAP.get(character);
      return ObjectUtils.isNotNull(mapChar) ? mapChar : character;
   }

   public static String getString(final char[] chars, final int startIndex, final int endIndex) {
      int len = endIndex - startIndex;
      return new String(chars, startIndex, len);
   }

   public static String getString(final char[] chars, final IWordResult wordResult) {
      return getString(chars, wordResult.startIndex(), wordResult.endIndex());
   }

   static {
      int size = "\u24b6\u24b7\u24b8\u24b9\u24ba\u24bb\u24bc\u24bd\u24be\u24bf\u24c0\u24c1\u24c2\u24c3\u24c4\u24c5\u24c6\u24c7\u24c8\u24c9\u24ca\u24cb\u24cc\u24cd\u24ce\u24cf\u24d0\u24d1\u24d2\u24d3\u24d4\u24d5\u24d6\u24d7\u24d8\u24d9\u24da\u24db\u24dc\u24dd\u24de\u24df\u24e0\u24e1\u24e2\u24e3\u24e4\u24e5\u24e6\u24e7\u24e8\u24e9\u249c\u249d\u249e\u249f\u24a0\u24a1\u24a2\u24a3\u24a4\u24a5\u24a6\u24a7\u24a8\u24a9\u24aa\u24ab\u24ac\u24ad\u24ae\u24af\u24b0\u24b1\u24b2\u24b3\u24b4\u24b5".length();

      for(int i = 0; i < size; ++i) {
         LETTER_MAP.put("\u24b6\u24b7\u24b8\u24b9\u24ba\u24bb\u24bc\u24bd\u24be\u24bf\u24c0\u24c1\u24c2\u24c3\u24c4\u24c5\u24c6\u24c7\u24c8\u24c9\u24ca\u24cb\u24cc\u24cd\u24ce\u24cf\u24d0\u24d1\u24d2\u24d3\u24d4\u24d5\u24d6\u24d7\u24d8\u24d9\u24da\u24db\u24dc\u24dd\u24de\u24df\u24e0\u24e1\u24e2\u24e3\u24e4\u24e5\u24e6\u24e7\u24e8\u24e9\u249c\u249d\u249e\u249f\u24a0\u24a1\u24a2\u24a3\u24a4\u24a5\u24a6\u24a7\u24a8\u24a9\u24aa\u24ab\u24ac\u24ad\u24ae\u24af\u24b0\u24b1\u24b2\u24b3\u24b4\u24b5".charAt(i), "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz".charAt(i));
      }

   }
}
