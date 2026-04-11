package com.kuma.boot.eventbus.disruptor.tmp3.util;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public abstract class StringUtils extends org.apache.commons.lang3.StringUtils {
   private static final String FOLDER_SEPARATOR = "/";
   private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
   private static final String TOP_PATH = "..";
   private static final String CURRENT_PATH = ".";
   private static final char EXTENSION_SEPARATOR = '.';
   public static String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
   private static final int[] allChineseScope = new int[]{1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600, Integer.MAX_VALUE};
   public static final char unknowChar = '*';
   private static final char[] allEnglishLetter = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z', '*'};

   public StringUtils() {
   }

   public static boolean isEmpty(String str) {
      if (str == null) {
         return true;
      } else if (str.length() == 0) {
         return true;
      } else {
         return "NULL".equals(str.toUpperCase());
      }
   }

   public static boolean isNotEmpty(String str) {
      return !isEmpty(str);
   }

   public static boolean isNull(String str) {
      return str == null || str.trim().length() == 0;
   }

   public static boolean isEmpty(Object str) {
      return str == null || "".equals(str);
   }

   public static boolean hasLength(CharSequence str) {
      return str != null && str.length() > 0;
   }

   public static boolean hasLength(String str) {
      return hasLength((CharSequence)str);
   }

   public static boolean hasText(CharSequence str) {
      if (!hasLength(str)) {
         return false;
      } else {
         int strLen = str.length();

         for(int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean hasText(String str) {
      return hasText((CharSequence)str);
   }

   public static boolean containsWhitespace(CharSequence str) {
      if (!hasLength(str)) {
         return false;
      } else {
         int strLen = str.length();

         for(int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean containsWhitespace(String str) {
      return containsWhitespace((CharSequence)str);
   }

   public static String trimWhitespace(String str) {
      if (!hasLength(str)) {
         return str;
      } else {
         StringBuilder sb = new StringBuilder(str);

         while(sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
         }

         while(sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
         }

         return sb.toString();
      }
   }

   public static String trimAllWhitespace(String str) {
      if (!hasLength(str)) {
         return str;
      } else {
         int len = str.length();
         StringBuilder sb = new StringBuilder(str.length());

         for(int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public static String trimLeadingWhitespace(String str) {
      if (!hasLength(str)) {
         return str;
      } else {
         StringBuilder sb = new StringBuilder(str);

         while(sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
         }

         return sb.toString();
      }
   }

   public static String trimTrailingWhitespace(String str) {
      if (!hasLength(str)) {
         return str;
      } else {
         StringBuilder sb = new StringBuilder(str);

         while(sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
         }

         return sb.toString();
      }
   }

   public static String trimLeadingCharacter(String str, char leadingCharacter) {
      if (!hasLength(str)) {
         return str;
      } else {
         StringBuilder sb = new StringBuilder(str);

         while(sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
            sb.deleteCharAt(0);
         }

         return sb.toString();
      }
   }

   public static String trimTrailingCharacter(String str, char trailingCharacter) {
      if (!hasLength(str)) {
         return str;
      } else {
         StringBuilder sb = new StringBuilder(str);

         while(sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
            sb.deleteCharAt(sb.length() - 1);
         }

         return sb.toString();
      }
   }

   public static boolean startsWithIgnoreCase(String str, String prefix) {
      if (str != null && prefix != null) {
         if (str.startsWith(prefix)) {
            return true;
         } else if (str.length() < prefix.length()) {
            return false;
         } else {
            String lcStr = str.substring(0, prefix.length()).toLowerCase();
            String lcPrefix = prefix.toLowerCase();
            return lcStr.equals(lcPrefix);
         }
      } else {
         return false;
      }
   }

   public static boolean endsWithIgnoreCase(String str, String suffix) {
      if (str != null && suffix != null) {
         if (str.endsWith(suffix)) {
            return true;
         } else if (str.length() < suffix.length()) {
            return false;
         } else {
            String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
            String lcSuffix = suffix.toLowerCase();
            return lcStr.equals(lcSuffix);
         }
      } else {
         return false;
      }
   }

   public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
      for(int j = 0; j < substring.length(); ++j) {
         int i = index + j;
         if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
            return false;
         }
      }

      return true;
   }

   public static int countOccurrencesOf(String str, String sub) {
      if (str != null && sub != null && str.length() != 0 && sub.length() != 0) {
         int count = 0;

         int idx;
         for(int pos = 0; (idx = str.indexOf(sub, pos)) != -1; pos = idx + sub.length()) {
            ++count;
         }

         return count;
      } else {
         return 0;
      }
   }

   public static String replace(String inString, String oldPattern, String newPattern) {
      if (hasLength(inString) && hasLength(oldPattern) && newPattern != null) {
         StringBuilder sb = new StringBuilder();
         int pos = 0;
         int index = inString.indexOf(oldPattern);

         for(int patLen = oldPattern.length(); index >= 0; index = inString.indexOf(oldPattern, pos)) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
         }

         sb.append(inString.substring(pos));
         return sb.toString();
      } else {
         return inString;
      }
   }

   public static String delete(String inString, String pattern) {
      return replace(inString, pattern, "");
   }

   public static String deleteAny(String inString, String charsToDelete) {
      if (hasLength(inString) && hasLength(charsToDelete)) {
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < inString.length(); ++i) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
               sb.append(c);
            }
         }

         return sb.toString();
      } else {
         return inString;
      }
   }

   public static String unqualify(String qualifiedName) {
      return unqualify(qualifiedName, '.');
   }

   public static String unqualify(String qualifiedName, char separator) {
      return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
   }

   public static String capitalize(String str) {
      return changeFirstCharacterCase(str, true);
   }

   public static String uncapitalize(String str) {
      return changeFirstCharacterCase(str, false);
   }

   private static String changeFirstCharacterCase(String str, boolean capitalize) {
      if (str != null && str.length() != 0) {
         StringBuilder sb = new StringBuilder(str.length());
         if (capitalize) {
            sb.append(Character.toUpperCase(str.charAt(0)));
         } else {
            sb.append(Character.toLowerCase(str.charAt(0)));
         }

         sb.append(str.substring(1));
         return sb.toString();
      } else {
         return str;
      }
   }

   public static String getFilename(String path) {
      if (path == null) {
         return null;
      } else {
         int separatorIndex = path.lastIndexOf("/");
         return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
      }
   }

   public static String getFilenameExtension(String path) {
      if (path == null) {
         return null;
      } else {
         int extIndex = path.lastIndexOf(46);
         if (extIndex == -1) {
            return null;
         } else {
            int folderIndex = path.lastIndexOf("/");
            return folderIndex > extIndex ? null : path.substring(extIndex + 1);
         }
      }
   }

   public static String stripFilenameExtension(String path) {
      if (path == null) {
         return null;
      } else {
         int extIndex = path.lastIndexOf(46);
         if (extIndex == -1) {
            return path;
         } else {
            int folderIndex = path.lastIndexOf("/");
            return folderIndex > extIndex ? path : path.substring(0, extIndex);
         }
      }
   }

   public static String applyRelativePath(String path, String relativePath) {
      int separatorIndex = path.lastIndexOf("/");
      if (separatorIndex != -1) {
         String newPath = path.substring(0, separatorIndex);
         if (!relativePath.startsWith("/")) {
            newPath = newPath + "/";
         }

         return newPath + relativePath;
      } else {
         return relativePath;
      }
   }

   public static String cleanPath(String path) {
      if (path == null) {
         return null;
      } else {
         String pathToUse = replace(path, "\\", "/");
         int prefixIndex = pathToUse.indexOf(":");
         String prefix = "";
         if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains("/")) {
               prefix = "";
            } else {
               pathToUse = pathToUse.substring(prefixIndex + 1);
            }
         }

         if (pathToUse.startsWith("/")) {
            prefix = prefix + "/";
            pathToUse = pathToUse.substring(1);
         }

         String[] pathArray = delimitedListToStringArray(pathToUse, "/");
         List<String> pathElements = new LinkedList();
         int tops = 0;

         for(int i = pathArray.length - 1; i >= 0; --i) {
            String element = pathArray[i];
            if (!".".equals(element)) {
               if ("..".equals(element)) {
                  ++tops;
               } else if (tops > 0) {
                  --tops;
               } else {
                  pathElements.add(0, element);
               }
            }
         }

         for(int i = 0; i < tops; ++i) {
            pathElements.add(0, "..");
         }

         return prefix + collectionToDelimitedString(pathElements, "/");
      }
   }

   public static boolean pathEquals(String path1, String path2) {
      return cleanPath(path1).equals(cleanPath(path2));
   }

   public static Locale parseLocaleString(String localeString) {
      String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
      String language = parts.length > 0 ? parts[0] : "";
      String country = parts.length > 1 ? parts[1] : "";
      validateLocalePart(language);
      validateLocalePart(country);
      String variant = "";
      if (parts.length > 2) {
         int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
         variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
         if (variant.startsWith("_")) {
            variant = trimLeadingCharacter(variant, '_');
         }
      }

      return !language.isEmpty() ? new Locale(language, country, variant) : null;
   }

   private static void validateLocalePart(String localePart) {
      for(int i = 0; i < localePart.length(); ++i) {
         char ch = localePart.charAt(i);
         if (ch != '_' && ch != ' ' && !Character.isLetterOrDigit(ch)) {
            throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
         }
      }

   }

   public static String toLanguageTag(Locale locale) {
      String var10000 = locale.getLanguage();
      return var10000 + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
   }

   public static TimeZone parseTimeZoneString(String timeZoneString) {
      TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
      if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT")) {
         throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
      } else {
         return timeZone;
      }
   }

   public static String[] addStringToArray(String[] array, String str) {
      if (ObjectUtils.isEmpty(array)) {
         return new String[]{str};
      } else {
         String[] newArr = new String[array.length + 1];
         System.arraycopy(array, 0, newArr, 0, array.length);
         newArr[array.length] = str;
         return newArr;
      }
   }

   public static String[] concatenateStringArrays(String[] array1, String[] array2) {
      if (ObjectUtils.isEmpty(array1)) {
         return array2;
      } else if (ObjectUtils.isEmpty(array2)) {
         return array1;
      } else {
         String[] newArr = new String[array1.length + array2.length];
         System.arraycopy(array1, 0, newArr, 0, array1.length);
         System.arraycopy(array2, 0, newArr, array1.length, array2.length);
         return newArr;
      }
   }

   public static String[] mergeStringArrays(String[] array1, String[] array2) {
      if (ObjectUtils.isEmpty(array1)) {
         return array2;
      } else if (ObjectUtils.isEmpty(array2)) {
         return array1;
      } else {
         List<String> result = new ArrayList();
         result.addAll(Arrays.asList(array1));

         for(String str : array2) {
            if (!result.contains(str)) {
               result.add(str);
            }
         }

         return toStringArray(result);
      }
   }

   public static String[] sortStringArray(String[] array) {
      if (ObjectUtils.isEmpty(array)) {
         return new String[0];
      } else {
         Arrays.sort(array);
         return array;
      }
   }

   public static String[] toStringArray(Collection<String> collection) {
      return collection == null ? null : (String[])collection.toArray(new String[collection.size()]);
   }

   public static String[] toStringArray(Enumeration<String> enumeration) {
      if (enumeration == null) {
         return null;
      } else {
         List<String> list = Collections.list(enumeration);
         return (String[])list.toArray(new String[list.size()]);
      }
   }

   public static String[] trimArrayElements(String[] array) {
      if (ObjectUtils.isEmpty(array)) {
         return new String[0];
      } else {
         String[] result = new String[array.length];

         for(int i = 0; i < array.length; ++i) {
            String element = array[i];
            result[i] = element != null ? element.trim() : null;
         }

         return result;
      }
   }

   public static String[] removeDuplicateStrings(String[] array) {
      if (ObjectUtils.isEmpty(array)) {
         return array;
      } else {
         Set<String> set = new TreeSet();

         for(String element : array) {
            set.add(element);
         }

         return toStringArray(set);
      }
   }

   public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
      return splitArrayElementsIntoProperties(array, delimiter, (String)null);
   }

   public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, String charsToDelete) {
      if (ObjectUtils.isEmpty(array)) {
         return null;
      } else {
         Properties result = new Properties();

         for(String element : array) {
            if (charsToDelete != null) {
               element = deleteAny(element, charsToDelete);
            }

            String[] splittedElement = split(element, delimiter);
            if (splittedElement != null) {
               result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
            }
         }

         return result;
      }
   }

   public static String[] tokenizeToStringArray(String str) {
      return tokenizeToStringArray(str, CONFIG_LOCATION_DELIMITERS, true, true);
   }

   public static String[] tokenizeToStringArray(String str, String delimiters) {
      return tokenizeToStringArray(str, delimiters, true, true);
   }

   public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
      if (str == null) {
         return null;
      } else {
         StringTokenizer st = new StringTokenizer(str, delimiters);
         List<String> tokens = new ArrayList();

         while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
               token = token.trim();
            }

            if (!ignoreEmptyTokens || token.length() > 0) {
               tokens.add(token);
            }
         }

         return toStringArray(tokens);
      }
   }

   public static String[] delimitedListToStringArray(String str, String delimiter) {
      return delimitedListToStringArray(str, delimiter, (String)null);
   }

   public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
      if (str == null) {
         return new String[0];
      } else if (delimiter == null) {
         return new String[]{str};
      } else {
         List<String> result = new ArrayList();
         if ("".equals(delimiter)) {
            for(int i = 0; i < str.length(); ++i) {
               result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
         } else {
            int delPos;
            int pos;
            for(pos = 0; (delPos = str.indexOf(delimiter, pos)) != -1; pos = delPos + delimiter.length()) {
               result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
            }

            if (str.length() > 0 && pos <= str.length()) {
               result.add(deleteAny(str.substring(pos), charsToDelete));
            }
         }

         return toStringArray(result);
      }
   }

   public static String[] commaDelimitedListToStringArray(String str) {
      return delimitedListToStringArray(str, ",");
   }

   public static Set<String> commaDelimitedListToSet(String str) {
      Set<String> set = new TreeSet();
      String[] tokens = commaDelimitedListToStringArray(str);

      for(String token : tokens) {
         set.add(token);
      }

      return set;
   }

   public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
      if (CollectionUtils.isEmpty(coll)) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         Iterator<?> it = coll.iterator();

         while(it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
               sb.append(delim);
            }
         }

         return sb.toString();
      }
   }

   public static String collectionToDelimitedString(Collection<?> coll, String delim) {
      return collectionToDelimitedString(coll, delim, "", "");
   }

   public static String collectionToCommaDelimitedString(Collection<?> coll) {
      return collectionToDelimitedString(coll, ",");
   }

   public static String arrayToDelimitedString(Object[] arr, String delim) {
      if (ObjectUtils.isEmpty(arr)) {
         return "";
      } else if (arr.length == 1) {
         return ObjectUtils.nullSafeToString(arr[0]);
      } else {
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < arr.length; ++i) {
            if (i > 0) {
               sb.append(delim);
            }

            sb.append(arr[i]);
         }

         return sb.toString();
      }
   }

   public static String arrayToCommaDelimitedString(Object[] arr) {
      return arrayToDelimitedString(arr, ",");
   }

   public static Map<String, String> getMapFromQueryParamString(String str) {
      Map<String, String> param = new HashMap();
      String[] keyValues = str.split("`");

      for(int i = 0; i < keyValues.length; ++i) {
      }

      return param;
   }

   public static String replaceAll(String src, String tar, String str) {
      StringBuilder sb = new StringBuilder();
      byte[] bytesSrc = src.getBytes();
      byte[] bytes = str.getBytes();
      int point = 0;

      for(int i = 0; i < bytes.length; ++i) {
         if (isStartWith(bytes, i, bytesSrc, 0)) {
            sb.append(new String(bytes, point, i));
            sb.append(tar);
            i += bytesSrc.length;
            point = i;
         }
      }

      sb.append(new String(bytes, point, bytes.length));
      return sb.toString();
   }

   private static boolean isStartWith(byte[] bytesSrc, int startSrc, byte[] bytesTar, int startTar) {
      for(int j = startTar; j < bytesTar.length; ++j) {
         if (bytesSrc[startSrc + j] != bytesTar[j]) {
            return false;
         }
      }

      return true;
   }

   public static char getFirstLetterFromChinessWord(String str) {
      char result = '*';
      String temp = str.toUpperCase();

      try {
         byte[] bytes = temp.getBytes("gbk");
         if (bytes[0] > 0) {
            return (char)bytes[0];
         }

         int gbkIndex = 0;

         for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)(bytes[i] - 160);
         }

         gbkIndex = bytes[0] * 100 + bytes[1];

         for(int i = 0; i < allEnglishLetter.length; ++i) {
            if (i == 22) {
               LogUtils.info(allEnglishLetter.length + " " + allChineseScope.length, new Object[0]);
            }

            if (gbkIndex >= allChineseScope[i] && gbkIndex < allChineseScope[i + 1]) {
               result = allEnglishLetter[i];
               break;
            }
         }
      } catch (Exception var6) {
      }

      return result;
   }

   public static String[] split(String src, char letter) {
      if (src == null) {
         return new String[0];
      } else {
         List<String> ret = new ArrayList();
         byte[] bytes = src.getBytes();
         int curPoint = 0;

         for(int i = 0; i < bytes.length; ++i) {
            if (bytes[i] == letter) {
               String s = new String(bytes, curPoint, i - curPoint);
               ret.add(s);
               curPoint = i + 1;
            }
         }

         if (ret.size() == 0) {
            return new String[]{src};
         } else {
            String[] retStr = new String[ret.size()];

            for(int i = 0; i < ret.size(); ++i) {
               retStr[i] = (String)ret.get(i);
            }

            return retStr;
         }
      }
   }

   public static String[] split(String toSplit, String delimiter) {
      if (hasLength(toSplit) && hasLength(delimiter)) {
         int offset = toSplit.indexOf(delimiter);
         if (offset < 0) {
            return null;
         } else {
            String beforeDelimiter = toSplit.substring(0, offset);
            String afterDelimiter = toSplit.substring(offset + delimiter.length());
            return new String[]{beforeDelimiter, afterDelimiter};
         }
      } else {
         return null;
      }
   }

   public static String[] splits(String toSplit, String regex) {
      return hasLength(toSplit) && hasLength(regex) ? toSplit.split(regex) : new String[0];
   }

   public static String removeLast(String str) {
      return isNull(str) ? str : str.substring(0, str.length() - 1);
   }

   public static String addQuotation(String str) {
      if (str == null) {
         return null;
      } else {
         StringBuilder newStr = new StringBuilder();
         String[] strs = split(str, ",");

         for(int i = 0; i < ((String[])Objects.requireNonNull(strs)).length; ++i) {
            if (i > 0) {
               newStr.append(",");
            }

            newStr.append("'").append(strs[i]).append("'");
         }

         return newStr.toString();
      }
   }

   public static String[] listToArray(List<String> list) {
      String[] strs = new String[list.size()];
      return (String[])list.toArray(strs);
   }

   public static String listToString(List<String> list, String separator) {
      return join(listToArray(list), separator);
   }

   public static String genRandomNum(int pwd_len) {
      int maxNum = 37;
      int count = 0;
      char[] str = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_'};
      StringBuilder pwd = new StringBuilder();
      Random r = new Random();

      while(count < pwd_len) {
         int i = Math.abs(r.nextInt(37));
         if (i < str.length) {
            pwd.append(str[i]);
            ++count;
         }
      }

      return pwd.toString();
   }

   public static String killNull(String str) {
      return str == null ? "" : str;
   }

   public static String parentheses(String source) {
      return source != null ? "(" + source + ")" : null;
   }

   public static String brackets(String source) {
      return source != null ? "[" + source + "]" : null;
   }

   public static String ditto(String source) {
      return source != null ? "\"" + source + "\"" : null;
   }

   public static String quote(String str) {
      return str != null ? "'" + str + "'" : null;
   }

   public static String quote(String[] array, String separator) {
      if (null != array && array.length != 0) {
         String[] last = new String[array.length];

         for(int i = 0; i < array.length; ++i) {
            last[i] = quote(array[i]);
         }

         return join(last, separator);
      } else {
         return "";
      }
   }

   public static Object quoteIfString(Object obj) {
      return obj instanceof String ? quote((String)obj) : obj;
   }

   public static String trimToAlphaString(String string) {
      return string != null && string.length() != 0 ? string.replaceAll("[^\\w]", "") : "";
   }

   public static String[] trimToAlphaStrings(String string) {
      if (string != null && string.length() != 0) {
         char[] chars = string.replaceAll("[^\\w]", "").toCharArray();
         String[] strs = new String[chars.length];

         for(int i = 0; i < chars.length; ++i) {
            strs[i] = String.valueOf(chars[i]);
         }

         return strs;
      } else {
         return new String[0];
      }
   }

   public static String trimToString(String str) {
      return str != null && str.trim().length() != 0 ? str.trim() : null;
   }
}
