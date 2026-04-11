package com.kuma.boot.data.mongodb.mongodb.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
   private static final String EMPTY_REGEX = "[\\s\\u00a0\\u2007\\u202f\\u0009-\\u000d\\u001c-\\u001f]+";
   private static final String NUM_REG = "(\\+|\\-)?\\s*\\d+(\\.\\d+)?";
   private static final String PUNCT_REG = "[^a-zA-Z0-9\\u4e00-\\u9fa5]";

   private StringUtil() {
   }

   public static String formatNull(String src) {
      return src != null && !"null".equals(src) ? src : "";
   }

   public static boolean isEmpty(String input) {
      return input == null || input.equals("") || input.matches("[\\s\\u00a0\\u2007\\u202f\\u0009-\\u000d\\u001c-\\u001f]+");
   }

   public static boolean isNotEmpty(String input) {
      return !isEmpty(input);
   }

   public static String toLowerCaseFirstOne(String s) {
      return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
   }

   public static String toUpperCaseFirstOne(String s) {
      return Character.isUpperCase(s.charAt(0)) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
   }

   public static boolean isNumber(String str) {
      if (isEmpty(str)) {
         return false;
      } else {
         return str.trim().matches("(\\+|\\-)?\\s*\\d+(\\.\\d+)?");
      }
   }

   public static boolean containUnreadableCode(String str) {
      return contain(str, "\\ufffd");
   }

   public static boolean containNumber(String str) {
      return contain(str, "\\d");
   }

   public static boolean containWord(String str) {
      return contain(str, "\\w");
   }

   public static boolean containPunct(String str) {
      return contain(str, "[^a-zA-Z0-9\\u4e00-\\u9fa5]");
   }

   public static boolean contain(String str, String regex) {
      if (!isEmpty(str) && !isEmpty(regex)) {
         if (str.trim().matches(regex)) {
            return true;
         } else {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            return matcher.find();
         }
      } else {
         return false;
      }
   }

   public static String replaceAll(String input, String regex, String replacement) {
      return Pattern.compile(regex, 2).matcher(input).replaceAll(replacement);
   }

   public static String removeAllSpace(String text) {
      return isEmpty(text) ? text : text.replaceAll("[ ]+", "");
   }

   public static String removeAllPunct(String str) {
      return isEmpty(str) ? str : str.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "");
   }

   public static int countMatches(String str, String sub) {
      if (!isEmpty(str) && !isEmpty(sub)) {
         int count = 0;

         int idx;
         for(idx = 0; (idx = str.indexOf(sub, idx)) != -1; idx += sub.length()) {
            ++count;
         }

         return count;
      } else {
         return 0;
      }
   }

   public static String substring(String str, int beginIndex, int endIndex) {
      if (isEmpty(str)) {
         return str;
      } else {
         int length = str.length();
         if (beginIndex < length && endIndex > 0 && beginIndex < endIndex) {
            if (beginIndex < 0) {
               beginIndex = 0;
            }

            if (endIndex > length) {
               endIndex = length;
            }

            return str.substring(beginIndex, endIndex);
         } else {
            return null;
         }
      }
   }

   public static Set<String> substring(String str, String sub) {
      if (!isEmpty(str) && !isEmpty(sub)) {
         Set<String> result = new HashSet();

         int idx;
         for(idx = 0; (idx = str.indexOf(sub, idx)) != -1; idx += sub.length()) {
            String temp = substring(str, idx - 1, idx + sub.length());
            if (!isEmpty(temp)) {
               temp = removeAllPunct(temp);
               if (!sub.equalsIgnoreCase(temp) && !containWord(temp)) {
                  result.add(temp);
               }
            }

            temp = substring(str, idx, idx + sub.length() + 1);
            if (!isEmpty(temp)) {
               temp = removeAllPunct(temp);
               if (!sub.equalsIgnoreCase(temp) && !containWord(temp)) {
                  result.add(temp);
               }
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public static String wrapXmlContent(String content) {
      if (isEmpty(content)) {
         return "";
      } else {
         StringBuilder result = new StringBuilder();

         for(int i = 0; i < content.length(); ++i) {
            char ch = content.charAt(i);
            if (ch == '\t' || ch == '\n' || ch == '\r' || ch >= ' ' && ch <= '\ud7ff' || ch >= '\ue000' && ch <= '\ufffd' || ch >= 65536 && ch <= 1114111) {
               result.append(ch);
            }
         }

         return result.toString();
      }
   }

   public static boolean overLength(String str) {
      if (isEmpty(str)) {
         return false;
      } else {
         return str.length() > 1;
      }
   }

   public static String specialStr(String str) {
      str = str.replaceAll("[^\\u4e00-\\u9fa5 | 0-9| a-zA-Z | \\.]+", " ").replaceAll("[\\.]{2,}", " ").trim();
      return str;
   }

   public static String replaceInValidateChar(String str) {
      return str.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5\\s+]", " ");
   }

   public static String[] toHexString(String str) {
      char[] chars = str.toCharArray();
      String[] result = new String[chars.length];

      for(int i = 0; i < chars.length; ++i) {
         result[i] = Integer.toHexString(chars[i]);
      }

      return result;
   }

   public static String getUuid() {
      return UUID.randomUUID().toString();
   }

   public static boolean isUrl(String src) {
      String regex = "http[s]?:\\/\\/([\\w-]+\\.[\\w-]+)(\\.[\\w-])+(:\\d{2,10})?.*";
      Pattern pattern = Pattern.compile(regex, 2);
      Matcher matcher = pattern.matcher(src);
      return matcher.matches();
   }

   public static String escapeSql(String str) {
      if (!isNotEmpty(str)) {
         return str;
      } else {
         StringBuffer strbuff = new StringBuffer();

         for(String s : str.split("")) {
            if (s.equals("%") || s.equals("_") || s.equals("\\")) {
               strbuff.append("\\");
            }

            strbuff.append(s);
         }

         return strbuff.toString();
      }
   }
}
