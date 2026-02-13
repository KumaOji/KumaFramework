/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.CollectionUtil
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.condition.Condition;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.common.ArrayPrimitiveUtils;
import com.kuma.boot.common.utils.common.CharUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.system.SystemUtils;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    public static final String LETTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWSXYZ";
    public static final String LETTERS_LOWER = "abcdefghijklmnopqrstuvwsxyz";
    private static final Pattern BLANK_PATTERN = Pattern.compile("\\s*|\t|\r|\n");
    public static final String Empty = "";
    public static final String EMPTY = "";
    public static final String EMPTY_JSON = "{}";
    public static final String BLANK = " ";
    public static final String NEW_LINE = "";

    private StringUtils() {
    }

    public static boolean isNotReturnLine(String line) {
        return !StringUtils.isReturnLine(line);
    }

    public static boolean isNullAndSpaceOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        return "".equals(obj.toString().trim());
    }

    public static boolean isReturnLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return true;
        }
        String trim = line.trim();
        if (StringUtils.isEmpty(trim)) {
            return true;
        }
        return "".equals(line);
    }

    public static boolean isUpperCase(String string) {
        char[] characters;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : characters = string.toCharArray()) {
            if (Character.isUpperCase(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean isLowerCase(String string) {
        char[] characters;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : characters = string.toCharArray()) {
            if (Character.isLowerCase(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean containsUppercase(String string) {
        char[] characters;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : characters = string.toCharArray()) {
            if (!Character.isUpperCase(c)) continue;
            return true;
        }
        return false;
    }

    public static boolean containsLowercase(String string) {
        char[] characters;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : characters = string.toCharArray()) {
            if (!Character.isLowerCase(c)) continue;
            return true;
        }
        return false;
    }

    public static boolean isLetter(String string) {
        return StringUtils.isCharsCondition(string, new Condition<Character>(){

            @Override
            public boolean condition(Character character) {
                return Character.isLowerCase(character.charValue()) || Character.isUpperCase(character.charValue());
            }
        });
    }

    public static boolean isDigit(String string) {
        return StringUtils.isCharsCondition(string, new Condition<Character>(){

            @Override
            public boolean condition(Character character) {
                return Character.isDigit(character.charValue());
            }
        });
    }

    public static boolean isDigitOrLetter(String string) {
        return StringUtils.isCharsCondition(string, new Condition<Character>(){

            @Override
            public boolean condition(Character character) {
                return CharUtils.isDigitOrLetter(character.charValue());
            }
        });
    }

    public static boolean isNumber(String string) {
        return StringUtils.isCharsCondition(string, new Condition<Character>(){

            @Override
            public boolean condition(Character character) {
                return CharUtils.isNumber(character.charValue());
            }
        });
    }

    private static boolean isCharsCondition(String string, Condition<Character> condition) {
        char[] chars;
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        for (char c : chars = string.toCharArray()) {
            if (condition.condition(Character.valueOf(c))) continue;
            return false;
        }
        return true;
    }

    public static boolean isEmpty(String string) {
        return null == string || "".equals(string);
    }

    public static boolean isEmptyTrim(String string) {
        if (StringUtils.isEmpty(string)) {
            return true;
        }
        String trim = StringUtils.trim(string);
        return StringUtils.isEmpty(trim);
    }

    public static boolean isNotEmptyTrim(String string) {
        return !StringUtils.isEmptyTrim(string);
    }

    public static boolean isEmptyJson(String json) {
        if (StringUtils.isEmptyTrim(json)) {
            return true;
        }
        String trim = json.trim();
        return EMPTY_JSON.equals(trim);
    }

    public static boolean isNotEmpty(String string) {
        return !StringUtils.isEmpty(string);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (Character.isWhitespace(str.charAt(i))) continue;
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    public static String[] splitByAnyBlank(String string) {
        if (StringUtils.isEmpty(string)) {
            return new String[0];
        }
        String pattern = "\\s+|\u0013";
        return string.split("\\s+|\u0013");
    }

    public static String trimAnyBlank(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        String trim = string.trim();
        return trim.replaceAll("\\s+|\u0013", "");
    }

    public static String replaceAnyBlank(String string, String replacement) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        Matcher m = BLANK_PATTERN.matcher(string);
        String result = m.replaceAll(replacement);
        result = result.replaceAll("\\u00A0", replacement);
        return result;
    }

    public static String replaceAnyBlank(String string) {
        return StringUtils.replaceAnyBlank(string, "");
    }

    public static String trimAnyPunctionAndSymbol(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        String trim = string.trim();
        return trim.replaceAll("\\p{P}|\\p{S}", "");
    }

    public static String getCamelCaseString(String inputString, boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        block3: for (int i = 0; i < inputString.length(); ++i) {
            char c = inputString.charAt(i);
            switch (c) {
                case ' ': 
                case '#': 
                case '$': 
                case '&': 
                case '-': 
                case '/': 
                case '@': 
                case '_': {
                    if (sb.length() <= 0) continue block3;
                    nextUpperCase = true;
                    continue block3;
                }
                default: {
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                        continue block3;
                    }
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb.toString();
    }

    public static String firstToLowerCase(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String firstToUpperCase(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String defaultEmpty(String string) {
        if (StringUtils.isEmpty(string)) {
            return "";
        }
        return string;
    }

    public static String join(Object ... array) {
        return StringUtils.join(array, ",");
    }

    public static String join(Object[] array, String separator) {
        int endIndex = ArrayUtils.getEndIndex(-1, array);
        return StringUtils.join(array, separator, 0, endIndex);
    }

    public static String join(String splitter, Object ... objects) {
        return StringUtils.join(objects, splitter);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        int noOfItems;
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        if ((noOfItems = endIndex - startIndex) < 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i <= endIndex; ++i) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] == null) continue;
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static <E> String join(Collection<E> collection, String splitter, int startIndex, int endIndex) {
        int i;
        if (CollectionUtil.isEmpty(collection)) {
            return "";
        }
        String actualSplitter = StringUtils.nullToDefault(splitter, "");
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<E> iterator = collection.iterator();
        for (i = 0; i < startIndex; ++i) {
            iterator.next();
        }
        stringBuilder.append(iterator.next().toString());
        for (i = startIndex; i < endIndex; ++i) {
            stringBuilder.append(actualSplitter).append(iterator.next().toString());
        }
        return stringBuilder.toString();
    }

    public static <E> String join(Collection<E> collection) {
        return StringUtils.join(collection, ",");
    }

    public static String camelToUnderline(String camelStr) {
        char[] chars;
        if (StringUtils.isEmpty(camelStr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chars = camelStr.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String underlineToCamel(String underlineStr) {
        if (StringUtils.isEmpty(underlineStr)) {
            return "";
        }
        int len = underlineStr.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            char c = underlineStr.charAt(i);
            if (c == '_') {
                if (++i >= len) continue;
                sb.append(Character.toUpperCase(underlineStr.charAt(i)));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String repeat(String component, int times) {
        if (StringUtils.isEmpty(component) || times <= 0) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < times; ++i) {
            stringBuffer.append(component);
        }
        return stringBuffer.toString();
    }

    public static String buildString(Object original, String middle, int prefixLength) {
        if (ObjectUtils.isNull(original)) {
            return null;
        }
        String string = original.toString();
        int stringLength = string.length();
        String prefix = "";
        String suffix = "";
        prefix = stringLength >= prefixLength ? string.substring(0, prefixLength) : string.substring(0, stringLength);
        int suffixLength = stringLength - prefix.length() - middle.length();
        if (suffixLength > 0) {
            suffix = string.substring(stringLength - suffixLength);
        }
        return prefix + middle + suffix;
    }

    public static String trim(String original) {
        if (StringUtils.isEmpty(original)) {
            return original;
        }
        return original.trim();
    }

    public static String nullToDefault(CharSequence str, String defaultStr) {
        return str == null ? defaultStr : str.toString();
    }

    public static String fill(String str, char filledChar, int len, boolean isPre) {
        int strLen = str.length();
        if (strLen > len) {
            return str;
        }
        String filledStr = StringUtils.repeat(String.valueOf(filledChar), len - strLen);
        return isPre ? filledStr.concat(str) : str.concat(filledStr);
    }

    public static String objectToString(Object object, String defaultWhenNull) {
        if (ObjectUtils.isNull(object)) {
            return defaultWhenNull;
        }
        Class<?> type = object.getClass();
        if (ClassTypeUtils.isArray(type)) {
            Object[] arrays = (Object[])object;
            return Arrays.toString(arrays);
        }
        return object.toString();
    }

    public static String objectToString(Object object) {
        return StringUtils.objectToString(object, null);
    }

    @Deprecated
    public static String times(String single, int times) {
        if (StringUtils.isEmpty(single)) {
            return single;
        }
        if (times <= 0) {
            return single;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; ++i) {
            stringBuilder.append(single);
        }
        return stringBuilder.toString();
    }

    public static String capitalFirst(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        if (string.length() <= 1) {
            return string.toUpperCase();
        }
        char capitalChar = Character.toUpperCase(string.charAt(0));
        return capitalChar + string.substring(1);
    }

    public static List<String> splitStrictly(String string, char splitUnit, int times) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (times <= 0) {
            return Collections.singletonList(string);
        }
        String split = CharUtils.repeat(splitUnit, times);
        String moreSplit = CharUtils.repeat(splitUnit, times + 1);
        List<Integer> splitIndexList = StringUtils.getIndexList(string, split);
        List<Integer> moreSplitIndexList = StringUtils.getIndexList(string, moreSplit);
        List<Integer> removeIndexList = StringUtils.getSerialFilterList(splitIndexList, moreSplitIndexList, times);
        List<Integer> trimIndexList = CollectionUtils.difference(splitIndexList, removeIndexList);
        return StringUtils.subStringList(string, trimIndexList, times);
    }

    private static List<Integer> getSerialFilterList(List<Integer> allList, List<Integer> filterList, int step) {
        ArrayList<Integer> resultList = new ArrayList<Integer>(filterList);
        for (Integer filter : filterList) {
            int nextVal;
            Integer indexVal;
            int startIndex = allList.indexOf(filter) + 1;
            int stepTimes = 1;
            for (int i = startIndex; i < allList.size() && (indexVal = allList.get(i)).equals(nextVal = step * stepTimes + filter); ++i) {
                resultList.add(nextVal);
                ++stepTimes;
            }
        }
        return resultList;
    }

    public static List<String> subStringList(String string, Collection<Integer> indexCollection, int ignoreLength) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(indexCollection)) {
            return Collections.singletonList(string);
        }
        ArrayList<String> resultList = new ArrayList<String>();
        int startIndex = 0;
        for (Integer index : indexCollection) {
            if (startIndex > string.length() - 1) {
                resultList.add("");
                break;
            }
            String subString = string.substring(startIndex, index);
            resultList.add(subString);
            startIndex = index + ignoreLength;
        }
        if (startIndex < string.length()) {
            String subString = string.substring(startIndex);
            resultList.add(subString);
        }
        return resultList;
    }

    public static List<Integer> getIndexList(String string, String split) {
        if (StringUtils.isEmpty(string) || StringUtils.isEmpty(split)) {
            return Collections.emptyList();
        }
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int startIndex = 0; startIndex < string.length() && (startIndex = string.indexOf(split, startIndex)) >= 0; startIndex += split.length()) {
            indexList.add(startIndex);
        }
        return indexList;
    }

    public static List<Integer> getIndexList(String string, char symbol, boolean ignoreDoubleQuotes) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        ArrayList<Integer> resultList = new ArrayList<Integer>();
        char[] chars = string.toCharArray();
        boolean doubleQuotesStart = false;
        char preChar = ' ';
        for (int i = 0; i < chars.length; ++i) {
            char currentChar = chars[i];
            if ('\\' != (preChar = ArrayPrimitiveUtils.getPreChar(preChar, currentChar)) && '\"' == currentChar) {
                boolean bl = doubleQuotesStart = !doubleQuotesStart;
            }
            if (currentChar != symbol) continue;
            if (ignoreDoubleQuotes) {
                if (doubleQuotesStart) continue;
                resultList.add(i);
                continue;
            }
            resultList.add(i);
        }
        return resultList;
    }

    public static List<String> splitByIndexes(String string, List<Integer> indexList) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(indexList)) {
            return Collections.singletonList(string);
        }
        ArrayList<String> resultList = new ArrayList<String>();
        int preIndex = 0;
        for (Integer anIndexList : indexList) {
            int currentIndex = anIndexList;
            if (currentIndex > preIndex) {
                resultList.add(string.substring(preIndex, currentIndex));
            }
            preIndex = currentIndex + 1;
        }
        int lastIndex = indexList.get(indexList.size() - 1);
        if (lastIndex + 1 < string.length()) {
            resultList.add(string.substring(lastIndex + 1));
        }
        return resultList;
    }

    public static byte[] stringToBytes(String string) {
        if (ObjectUtils.isNull(string)) {
            return null;
        }
        return string.getBytes();
    }

    public static String bytesToString(byte[] bytes) {
        if (ArrayPrimitiveUtils.isEmpty(bytes)) {
            return null;
        }
        return new String(bytes);
    }

    public static String[] splitToStringArray(String string, String splitter) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        return string.split(splitter);
    }

    public static String[] splitToStringArray(String string) {
        return StringUtils.splitToStringArray(string, ",");
    }

    public static String join(byte[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Byte> lists = ArrayPrimitiveUtils.toList(array, new Handler<Byte, Byte>(){

            @Override
            public Byte handle(Byte value) {
                return value;
            }
        });
        return StringUtils.join(lists, splitter);
    }

    public static String join(char[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Character> lists = ArrayPrimitiveUtils.toList(array, new Handler<Character, Character>(){

            @Override
            public Character handle(Character value) {
                return value;
            }
        });
        return StringUtils.join(lists, splitter);
    }

    public static String join(short[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Short> lists = ArrayPrimitiveUtils.toList(array, value -> value);
        return StringUtils.join(lists, splitter);
    }

    public static String join(long[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Long> lists = ArrayPrimitiveUtils.toList(array, value -> value);
        return StringUtils.join(lists, splitter);
    }

    public static String join(float[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Float> lists = ArrayPrimitiveUtils.toList(array, value -> value);
        return StringUtils.join(lists, splitter);
    }

    public static String join(double[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Double> lists = ArrayPrimitiveUtils.toList(array, new Handler<Double, Double>(){

            @Override
            public Double handle(Double value) {
                return value;
            }
        });
        return StringUtils.join(lists, splitter);
    }

    public static String join(boolean[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Boolean> lists = ArrayPrimitiveUtils.toList(array, new Handler<Boolean, Boolean>(){

            @Override
            public Boolean handle(Boolean value) {
                return value;
            }
        });
        return StringUtils.join(lists, splitter);
    }

    public static String join(int[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Integer> integers = ArrayPrimitiveUtils.toList(array, new Handler<Integer, Integer>(){

            @Override
            public Integer handle(Integer integer) {
                return integer;
            }
        });
        return StringUtils.join(integers, splitter);
    }

    private static String getSplitter(String ... splitters) {
        if (ArrayUtils.isEmpty(splitters)) {
            return ",";
        }
        return splitters[0];
    }

    public static List<String> splitToList(String string, String splitter) {
        ArgUtils.notEmpty(splitter, "splitter");
        if (StringUtils.isEmpty(string)) {
            return new ArrayList<String>();
        }
        String[] strings = string.split(splitter);
        return ArrayUtils.toList(strings);
    }

    public static List<String> splitToList(String string, char c) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        char[] chars = string.toCharArray();
        return StringUtils.splitToList(chars, c);
    }

    public static List<String> splitToList(char[] chars, char c) {
        if (ArrayPrimitiveUtils.isEmpty(chars)) {
            return Collections.emptyList();
        }
        ArrayList<String> resultList = new ArrayList<String>();
        StringBuilder stringBuilder = new StringBuilder();
        for (char cs : chars) {
            if (c == cs) {
                resultList.add(stringBuilder.toString());
                stringBuilder.setLength(0);
                continue;
            }
            stringBuilder.append(cs);
        }
        if (!stringBuilder.isEmpty()) {
            resultList.add(stringBuilder.toString());
        }
        return resultList;
    }

    public static List<String> splitToList(String string) {
        return StringUtils.splitToList(string, ",");
    }

    public static Character[] toCharacterArray(String string) {
        char[] chars = string.toCharArray();
        Character[] newArray = new Character[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            newArray[i] = Character.valueOf(chars[i]);
        }
        return newArray;
    }

    public static List<Character> toCharacterList(String string) {
        char[] chars = string.toCharArray();
        ArrayList<Character> newList = new ArrayList<Character>(chars.length);
        for (char aChar : chars) {
            newList.add(Character.valueOf(aChar));
        }
        return newList;
    }

    public static List<String> toCharStringList(String string) {
        if (StringUtils.isEmpty(string)) {
            return new ArrayList<String>();
        }
        char[] chars = string.toCharArray();
        return ArrayPrimitiveUtils.toList(chars, new Handler<Character, String>(){

            @Override
            public String handle(Character character) {
                return String.valueOf(character);
            }
        });
    }

    public static Set<Character> toCharSet(String string) {
        if (StringUtils.isEmpty(string)) {
            return new HashSet<Character>();
        }
        char[] chars = string.toCharArray();
        HashSet<Character> set = new HashSet<Character>();
        for (char c : chars) {
            set.add(Character.valueOf(c));
        }
        return set;
    }

    public static String toHalfWidth(String string) {
        return StringUtils.characterHandler(string, new Handler<Character, Character>(){

            @Override
            public Character handle(Character character) {
                return Character.valueOf(CharUtils.toHalfWidth(character.charValue()));
            }
        });
    }

    public static String toFullWidth(String string) {
        return StringUtils.characterHandler(string, new Handler<Character, Character>(){

            @Override
            public Character handle(Character character) {
                return Character.valueOf(CharUtils.toFullWidth(character.charValue()));
            }
        });
    }

    private static String characterHandler(String string, Handler<Character, Character> handler) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        char[] resultChars = new char[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            resultChars[i] = handler.handle(Character.valueOf(chars[i])).charValue();
        }
        return new String(resultChars);
    }

    public static String trimNotChinese(String string) {
        if (StringUtils.isEmptyTrim(string)) {
            return "";
        }
        char[] chars = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        char[] cArray = chars;
        int n = cArray.length;
        for (int i = 0; i < n; ++i) {
            Character character = Character.valueOf(cArray[i]);
            if (!CharUtils.isChinese(character.charValue())) continue;
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }

    public static String valueOf(Object object) {
        if (ObjectUtils.isNull(object)) {
            return null;
        }
        return String.valueOf(object);
    }

    public static String leftPadding(String original, int targetLength, char unit) {
        ArgUtils.notNull(original, "original");
        int originalLength = original.length();
        if (originalLength >= targetLength) {
            return original;
        }
        StringBuilder stringBuilder = new StringBuilder(targetLength);
        for (int i = originalLength; i < targetLength; ++i) {
            stringBuilder.append(unit);
        }
        stringBuilder.append(original);
        return stringBuilder.toString();
    }

    public static String leftPadding(String original, int targetLength) {
        return StringUtils.leftPadding(original, targetLength, '0');
    }

    public static Character getFirstChar(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Character.valueOf(text.charAt(0));
    }

    public static String emptyToNull(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return text;
    }

    public static Boolean toBool(String text) {
        return "YES".equalsIgnoreCase(text) || "TRUE".equalsIgnoreCase(text) || "1".equalsIgnoreCase(text);
    }

    public static Character toChar(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Character.valueOf(text.charAt(0));
    }

    public static Byte toByte(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Byte.valueOf(text);
    }

    public static Short toShort(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Short.valueOf(text);
    }

    public static Integer toInt(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Integer.valueOf(text);
    }

    public static Long toLong(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Long.valueOf(text);
    }

    public static Float toFloat(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Float.valueOf(text);
    }

    public static Double toDouble(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return Double.valueOf(text);
    }

    public static BigInteger toBigInteger(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return new BigInteger(text);
    }

    public static BigDecimal toBigDecimal(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return new BigDecimal(text);
    }

    public static Date toDate(String text, String dateFormat) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return DateUtils.getFormatDate(text, dateFormat);
    }

    public static Date toDate(String text) {
        return StringUtils.toDate(text, "yyyyMMdd");
    }

    public static String toString(Date date, String format) {
        return DateUtils.getDateFormat(date, format);
    }

    public static String toString(Date date) {
        return StringUtils.toString(date, "yyyyMMdd");
    }

    public static String toString(Object object) {
        if (null == object) {
            return null;
        }
        return object.toString();
    }

    public static String toString(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new BootException(e);
        }
    }

    public static String toString(byte[] bytes) {
        return StringUtils.toString(bytes, "UTF-8");
    }

    public static byte[] getBytes(String text, String charset) {
        try {
            return text.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new BootException(e);
        }
    }

    public static byte[] getBytes(String text) {
        return StringUtils.getBytes(text, "UTF-8");
    }

    public static boolean isEnglish(String text) {
        char[] chars;
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        for (char c : chars = text.toCharArray()) {
            if (CharUtils.isEnglish(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean isChinese(String text) {
        char[] chars;
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        for (char c : chars = text.toCharArray()) {
            if (CharUtils.isChinese(c)) continue;
            return false;
        }
        return true;
    }

    public static String packageToPath(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return packageName;
        }
        return packageName.replaceAll("\\.", "/");
    }

    public static String subString(String text, int startIndex, int length) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        if (length <= 0) {
            return null;
        }
        int endIndex = startIndex + length;
        if (endIndex > text.length()) {
            endIndex = text.length();
        }
        return text.substring(startIndex, endIndex);
    }

    public static List<String> contentToLines(String content) {
        if (content == null) {
            return null;
        }
        String[] strings = content.split("\\r?\\n");
        return ArrayUtils.toList(strings);
    }

    public static String linesToContent(List<String> lines) {
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lines.size() - 1; ++i) {
            stringBuilder.append(lines.get(i)).append(SystemUtils.getLineSeparator());
        }
        stringBuilder.append(lines.get(lines.size() - 1));
        return stringBuilder.toString();
    }

    public static List<String> splitByLength(String text, int limitSize) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }
        int totalLength = text.length();
        int times = totalLength / limitSize;
        if (totalLength % limitSize != 0) {
            ++times;
        }
        ArrayList<String> resultList = new ArrayList<String>(times);
        for (int i = 0; i < times; ++i) {
            int startIndex = i * limitSize;
            int endIndex = (i + 1) * limitSize;
            if (endIndex > totalLength) {
                endIndex = totalLength;
            }
            String subText = text.substring(startIndex, endIndex);
            resultList.add(subText);
        }
        return resultList;
    }

    public static List<String> getAllSubStrList(String text, int minStepLen) {
        ArgUtils.positive(minStepLen, "stepLen");
        if (StringUtils.isEmpty(text) || minStepLen > text.length()) {
            return Collections.emptyList();
        }
        ArrayList<String> list = new ArrayList<String>();
        int length = text.length();
        for (int i = minStepLen; i < length; ++i) {
            for (int j = 0; j <= length - i; ++j) {
                String subStr = text.substring(j, j + i);
                list.add(subStr);
            }
        }
        return list;
    }

    public static String replaceEmoji(String text, String replacement) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        return text.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", replacement);
    }

    public static String replaceEmoji(String text) {
        return StringUtils.replaceEmoji(text, "");
    }

    public static Set<Character> getCharSet(String text) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptySet();
        }
        char[] chars = text.toCharArray();
        HashSet<Character> characterSet = new HashSet<Character>(chars.length);
        for (int i = 0; i < chars.length; ++i) {
            characterSet.add(Character.valueOf(chars[i]));
        }
        return characterSet;
    }

    public static String subWithBytes(String text, int bytesLen, String charset) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        try {
            byte[] bytes = text.getBytes(charset);
            if (bytes.length <= bytesLen) {
                return text;
            }
            return new String(bytes, 0, bytesLen, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String subWithBytes(String text, int bytesLen) {
        return StringUtils.subWithBytes(text, bytesLen, "UTF-8");
    }
}

