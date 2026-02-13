/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.http.HttpUtil
 *  cn.hutool.json.JSONObject
 *  cn.hutool.json.JSONUtil
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.HtmlUtils
 */
package com.kuma.boot.common.utils.lang;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.kuma.boot.common.enums.RandomEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.RandomHolder;
import com.kuma.boot.common.support.condition.Condition;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.support.tuple.impl.Pair;
import com.kuma.boot.common.utils.collection.ArrayPrimitiveUtils;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.lang.CharUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.number.NumberUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.common.utils.reflect.ReflectFieldUtils;
import com.kuma.boot.common.utils.system.SystemUtils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.util.HtmlUtils;

public final class StringUtils
extends StrUtil {
    public static final String LETTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWSXYZ";
    public static final String LETTERS_LOWER = "abcdefghijklmnopqrstuvwsxyz";
    private static final Pattern BLANK_PATTERN = Pattern.compile("\\s*|\t|\r|\n");
    public static final String EMPTY = "";
    public static final String EMPTY_JSON = "{}";
    public static final String BLANK = " ";
    public static final String NEW_LINE = "";
    private static final char SEPARATOR = '_';
    private static final String UNKNOWN = "unknown";
    private static final Pattern SPECIAL_CHARS_REGEX = Pattern.compile("[`'\"|/,;()-+*%#\u00b7\u2022\ufffd\u3000\\s]");
    private static final int PAD_LIMIT = 8192;

    public static String replace(String str, String replacedStr, int start, int end) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(str);
        stringBuffer.replace(start, end, replacedStr);
        return stringBuffer.toString();
    }

    public static boolean isTrue(@Nullable Boolean object) {
        return Boolean.TRUE.equals(object);
    }

    public static boolean isFalse(@Nullable Boolean object) {
        return object == null || Boolean.FALSE.equals(object);
    }

    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !StringUtils.isEmpty(array);
    }

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        return Objects.equals(o1, o2);
    }

    public static boolean isNotEqual(Object o1, Object o2) {
        return !Objects.equals(o1, o2);
    }

    public static int hashCode(@Nullable Object obj) {
        return Objects.hashCode(obj);
    }

    public static Object defaultIfNull(@Nullable Object object, Object defaultValue) {
        return object != null ? object : defaultValue;
    }

    public static @Nullable String toStr(@Nullable Object object) {
        return StringUtils.toStr(object, null);
    }

    public static @Nullable String toStr(@Nullable Object object, @Nullable String defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence)object).toString();
        }
        return String.valueOf(object);
    }

    public static int toInt(@Nullable Object object) {
        return StringUtils.toInt(object, 0);
    }

    public static int toInt(@Nullable Object object, int defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).intValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static long toLong(@Nullable Object object) {
        return StringUtils.toLong(object, 0L);
    }

    public static long toLong(@Nullable Object object, long defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).longValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static float toFloat(@Nullable Object object) {
        return StringUtils.toFloat(object, 0.0f);
    }

    public static float toFloat(@Nullable Object object, float defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).floatValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Float.parseFloat(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static double toDouble(@Nullable Object object) {
        return StringUtils.toDouble(object, 0.0);
    }

    public static double toDouble(@Nullable Object object, double defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static byte toByte(@Nullable Object object) {
        return StringUtils.toByte(object, (byte)0);
    }

    public static byte toByte(@Nullable Object object, byte defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Byte.parseByte(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static short toShort(@Nullable Object object) {
        return StringUtils.toShort(object, (short)0);
    }

    public static short toShort(@Nullable Object object, short defaultValue) {
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            try {
                return Short.parseShort(value);
            }
            catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static @Nullable Boolean toBoolean(@Nullable Object object) {
        return StringUtils.toBoolean(object, null);
    }

    public static @Nullable Boolean toBoolean(@Nullable Object object, @Nullable Boolean defaultValue) {
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object instanceof CharSequence) {
            String value = ((CharSequence)object).toString();
            if ("true".equalsIgnoreCase(value) || "y".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
                return true;
            }
            if ("false".equalsIgnoreCase(value) || "n".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
                return false;
            }
        }
        return defaultValue;
    }

    public static boolean isSameType(Object one, Object two) {
        if (ObjectUtils.isNull(one) || StringUtils.isNull(two)) {
            return false;
        }
        Class<?> clazzOne = one.getClass();
        return clazzOne.isInstance(two);
    }

    public static boolean isNotSameType(Object one, Object two) {
        return !StringUtils.isSameType(one, two);
    }

    public static boolean isNull(Object object) {
        return null == object;
    }

    public static boolean isNotNull(Object object) {
        return !StringUtils.isNull(object);
    }

    public static boolean isEmpty(Object object) {
        if (StringUtils.isNull(object)) {
            return true;
        }
        if (object instanceof String) {
            String string = (String)object;
            return StringUtils.isEmpty(string);
        }
        if (object instanceof Collection) {
            Collection collection = (Collection)object;
            return CollectionUtils.isEmpty(collection);
        }
        if (object instanceof Map) {
            Map map = (Map)object;
            return MapUtils.isEmpty(map);
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        return false;
    }

    public static boolean isNotEmpty(Object object) {
        return !StringUtils.isEmpty(object);
    }

    public static boolean isEquals(Object except, Object real) {
        if (StringUtils.isNotSameType(except, real)) {
            return false;
        }
        Class<?> exceptClass = except.getClass();
        Class<?> realClass = except.getClass();
        if (exceptClass.isPrimitive() && realClass.isPrimitive() && except != real) {
            return false;
        }
        if (ClassTypeUtils.isArray(exceptClass) && ClassTypeUtils.isArray(realClass)) {
            Object[] exceptArray = (Object[])except;
            Object[] realArray = (Object[])real;
            return Arrays.equals(exceptArray, realArray);
        }
        if (ClassTypeUtils.isMap(exceptClass) && ClassTypeUtils.isMap(realClass)) {
            Map exceptMap = (Map)except;
            Map realMap = (Map)real;
            return exceptMap.equals(realMap);
        }
        return except.equals(real);
    }

    public static boolean isNotEquals(Object except, Object real) {
        return !StringUtils.isEquals(except, real);
    }

    public static boolean isNull(Object object, Object ... others) {
        if (StringUtils.isNull(object)) {
            if (ArrayUtils.isNotEmpty(others)) {
                for (Object other : others) {
                    if (!StringUtils.isNotNull(other)) continue;
                    return false;
                }
                return true;
            }
            return true;
        }
        return false;
    }

    public static boolean isEqualsOrNull(Object left, Object right) {
        if (StringUtils.isNull(left, right)) {
            return true;
        }
        if (StringUtils.isNull(left) || StringUtils.isNull(right)) {
            return false;
        }
        return StringUtils.isEquals(left, right);
    }

    public static <R> List<R> toList(Object object, Handler<Object, R> handler) {
        if (StringUtils.isNull(object)) {
            return Collections.emptyList();
        }
        Class<?> clazz = object.getClass();
        if (ClassTypeUtils.isCollection(clazz)) {
            Collection collection = (Collection)object;
            return CollectionUtils.toList(collection, handler);
        }
        if (clazz.isArray()) {
            return ArrayUtils.toList(object, handler);
        }
        throw new UnsupportedOperationException("Not support foreach() for class: " + clazz.getName());
    }

    public static Class<?> getClass(Object object) {
        if (StringUtils.isNull(object)) {
            return null;
        }
        return object.getClass();
    }

    public static void emptyToNull(Object object) {
        if (null == object) {
            return;
        }
        List<Field> fieldList = ClassUtils.getAllFieldList(object.getClass());
        for (Field field : fieldList) {
            Object value = ReflectFieldUtils.getValue(field, object);
            if (!StringUtils.isEmpty(value)) continue;
            ReflectFieldUtils.setValue(field, object, null);
        }
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        Map<String, Field> sourceFieldMap = ClassUtils.getAllFieldMap(source.getClass());
        Map<String, Field> targetFieldMap = ClassUtils.getAllFieldMap(target.getClass());
        for (Map.Entry<String, Field> entry : sourceFieldMap.entrySet()) {
            String sourceFieldName = entry.getKey();
            Field sourceField = entry.getValue();
            Field targetField = targetFieldMap.get(sourceFieldName);
            if (targetField == null || !ClassUtils.isAssignable(sourceField.getType(), targetField.getType())) continue;
            Object sourceVal = ReflectFieldUtils.getValue(sourceField, source);
            ReflectFieldUtils.setValue(targetField, target, sourceVal);
        }
    }

    public static boolean isSameValue(Object valueOne, Object valueTwo) {
        if (valueOne == null && valueTwo == null) {
            return true;
        }
        if (valueOne == null || valueTwo == null) {
            return false;
        }
        return valueOne.equals(valueTwo);
    }

    private StringUtils() {
    }

    public static boolean isNotReturnLine(String line) {
        return !StringUtils.isReturnLine(line);
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
        if (str == null || str.trim().length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String firstToUpperCase(String str) {
        if (str == null || str.trim().length() == 0) {
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
        if (CollectionUtils.isEmpty(collection)) {
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
        ArrayList resultList = Lists.newArrayList();
        resultList.addAll(filterList);
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
        if (CollectionUtils.isEmpty(indexCollection)) {
            return Collections.singletonList(string);
        }
        ArrayList resultList = Lists.newArrayList();
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
        ArrayList indexList = Lists.newArrayList();
        for (int startIndex = 0; startIndex < string.length() && (startIndex = string.indexOf(split, startIndex)) >= 0; startIndex += split.length()) {
            indexList.add(startIndex);
        }
        return indexList;
    }

    public static List<Integer> getIndexList(String string, char symbol, boolean ignoreDoubleQuotes) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        ArrayList resultList = Lists.newArrayList();
        char[] chars = string.toCharArray();
        boolean doubleQuotesStart = false;
        char preChar = ' ';
        for (int i = 0; i < chars.length; ++i) {
            char currentChar = chars[i];
            if ('\\' != (preChar = StringUtils.getPreChar(preChar, currentChar)) && '\"' == currentChar) {
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

    @Deprecated
    private static char getPreChar(char preChar, char currentChar) {
        if ('\\' == preChar && '\\' == currentChar) {
            return ' ';
        }
        return currentChar;
    }

    public static List<String> splitByIndexes(String string, List<Integer> indexList) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(indexList)) {
            return Collections.singletonList(string);
        }
        ArrayList resultList = Lists.newArrayList();
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
        List<Short> lists = ArrayPrimitiveUtils.toList(array, new Handler<Short, Short>(){

            @Override
            public Short handle(Short value) {
                return value;
            }
        });
        return StringUtils.join(lists, splitter);
    }

    public static String join(long[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Long> lists = ArrayPrimitiveUtils.toList(array, new Handler<Long, Long>(){

            @Override
            public Long handle(Long value) {
                return value;
            }
        });
        return StringUtils.join(lists, splitter);
    }

    public static String join(float[] array, String ... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return "";
        }
        String splitter = StringUtils.getSplitter(splitters);
        List<Float> lists = ArrayPrimitiveUtils.toList(array, new Handler<Float, Float>(){

            @Override
            public Float handle(Float value) {
                return value;
            }
        });
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
            return Lists.newArrayList();
        }
        Object[] strings = string.split(splitter);
        return Lists.newArrayList((Object[])strings);
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
            return Lists.newArrayList();
        }
        char[] chars = string.toCharArray();
        return ArrayPrimitiveUtils.toList(chars, new Handler<Character, String>(){

            @Override
            public String handle(Character character) {
                return String.valueOf(character);
            }
        });
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
        if (CollectionUtils.isEmpty(lines)) {
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

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '_') {
                upperCase = true;
                continue;
            }
            if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = StringUtils.toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            boolean nextUpperCase = true;
            if (i < s.length() - 1) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }
            if (i > 0 && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append('_');
                }
                upperCase = true;
            } else {
                upperCase = false;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String getCityInfo(String ip) {
        String api = String.format("http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true", ip);
        JSONObject object = JSONUtil.parseObj((String)HttpUtil.get((String)api));
        return (String)object.get((Object)"addr", String.class);
    }

    public static String getWeekDay() {
        String[] weekDays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(7) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static String firstCharToLower(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] + 32);
            return new String(arr);
        }
        return str;
    }

    public static String firstCharToUpper(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] - 32);
            return new String(arr);
        }
        return str;
    }

    public static boolean isBlank(@Nullable CharSequence cs) {
        return !StringUtils.hasText(cs);
    }

    public static boolean isNotBlank(@Nullable CharSequence cs) {
        return StringUtils.hasText(cs);
    }

    public static boolean isAnyBlank(CharSequence ... css) {
        if (ObjectUtils.isEmpty((Object[])css)) {
            return true;
        }
        return Stream.of(css).anyMatch(StringUtils::isBlank);
    }

    public static boolean isAnyBlank(Collection<CharSequence> css) {
        if (CollectionUtils.isEmpty(css)) {
            return true;
        }
        return css.stream().anyMatch(StringUtils::isBlank);
    }

    public static boolean isNoneBlank(CharSequence ... css) {
        if (ObjectUtils.isEmpty((Object[])css)) {
            return false;
        }
        return Stream.of(css).allMatch(StringUtils::isNotBlank);
    }

    public static boolean isNoneBlank(Collection<CharSequence> css) {
        if (CollectionUtils.isEmpty(css)) {
            return false;
        }
        return css.stream().allMatch(StringUtils::isNotBlank);
    }

    public static boolean isAnyNotBlank(CharSequence ... css) {
        if (ObjectUtils.isEmpty((Object[])css)) {
            return false;
        }
        return Stream.of(css).anyMatch(xva$0 -> StringUtils.isNoneBlank(xva$0));
    }

    public static boolean isAnyNotBlank(Collection<CharSequence> css) {
        if (CollectionUtils.isEmpty(css)) {
            return false;
        }
        return css.stream().anyMatch(xva$0 -> StringUtils.isNoneBlank(xva$0));
    }

    public static boolean isNumeric(CharSequence cs) {
        if (StringUtils.isBlank(cs)) {
            return false;
        }
        int i = cs.length();
        while (--i >= 0) {
            char chr = cs.charAt(i);
            if (chr >= '0' && chr <= '9') continue;
            return false;
        }
        return true;
    }

    public static boolean startWith(CharSequence cs, char c) {
        return cs.charAt(0) == c;
    }

    public static boolean endWith(CharSequence cs, char c) {
        return cs.charAt(cs.length() - 1) == c;
    }

    public static String format(@Nullable String message, @Nullable Map<String, ?> params) {
        int end;
        int start;
        if (message == null) {
            return "";
        }
        if (params == null || params.isEmpty()) {
            return message;
        }
        StringBuilder sb = new StringBuilder((int)((double)message.length() * 1.5));
        int cursor = 0;
        while ((start = message.indexOf("${", cursor)) != -1 && (end = message.indexOf(125, start)) != -1) {
            sb.append(message, cursor, start);
            String key = message.substring(start + 2, end);
            Object value = params.get(key.strip());
            sb.append((Object)(value == null ? "" : value));
            cursor = end + 1;
        }
        sb.append(message.substring(cursor));
        return sb.toString();
    }

    public static String format(@Nullable String message, Object ... arguments) {
        int end;
        int start;
        if (message == null) {
            return "";
        }
        if (arguments == null || arguments.length == 0) {
            return message;
        }
        StringBuilder sb = new StringBuilder((int)((double)message.length() * 1.5));
        int cursor = 0;
        int argsLength = arguments.length;
        for (int index = 0; (start = message.indexOf(123, cursor)) != -1 && (end = message.indexOf(125, start)) != -1 && index < argsLength; ++index) {
            sb.append(message, cursor, start);
            sb.append(arguments[index]);
            cursor = end + 1;
        }
        sb.append(message.substring(cursor));
        return sb.toString();
    }

    public static String format(long nanos) {
        if (nanos < 1L) {
            return "0ms";
        }
        double millis = (double)nanos / 1000000.0;
        if (millis > 1000.0) {
            return String.format("%.3fs", millis / 1000.0);
        }
        return String.format("%.3fms", millis);
    }

    public static String join(Collection<?> coll) {
        return org.springframework.util.StringUtils.collectionToCommaDelimitedString(coll);
    }

    public static String join(Collection<?> coll, String delim) {
        return org.springframework.util.StringUtils.collectionToDelimitedString(coll, (String)delim);
    }

    public static String join(Object[] arr, String delim) {
        return org.springframework.util.StringUtils.arrayToDelimitedString((Object[])arr, (String)delim);
    }

    public static String[] split(@Nullable String str, @Nullable String delimiter) {
        return org.springframework.util.StringUtils.delimitedListToStringArray((String)str, (String)delimiter);
    }

    public static String[] splitTrim(@Nullable String str, @Nullable String delimiter) {
        return org.springframework.util.StringUtils.delimitedListToStringArray((String)str, (String)delimiter, (String)" \t\n\n\f");
    }

    public static boolean simpleMatch(@Nullable String pattern, @Nullable String str) {
        return PatternMatchUtils.simpleMatch((String)pattern, (String)str);
    }

    public static boolean simpleMatch(@Nullable String[] patterns, String str) {
        return PatternMatchUtils.simpleMatch((String[])patterns, (String)str);
    }

    public static String getUUID() throws UnsupportedEncodingException {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long lsb = random.nextLong();
        long msb = random.nextLong();
        byte[] buf = new byte[32];
        StringUtils.formatUnsignedLong(lsb, buf, 20, 12);
        StringUtils.formatUnsignedLong(lsb >>> 48, buf, 16, 4);
        StringUtils.formatUnsignedLong(msb, buf, 12, 4);
        StringUtils.formatUnsignedLong(msb >>> 16, buf, 8, 4);
        StringUtils.formatUnsignedLong(msb >>> 32, buf, 0, 8);
        return new String(buf, StandardCharsets.UTF_8);
    }

    private static void formatUnsignedLong(long val, byte[] buf, int offset, int len) {
        int charPos = offset + len;
        int radix = 16;
        int mask = radix - 1;
        do {
            buf[--charPos] = NumberUtils.DIGITS[(int)val & mask];
            val >>>= 4;
        } while (charPos > offset);
    }

    public static String escapeHtml(String html) {
        return HtmlUtils.htmlEscape((String)html);
    }

    public static @Nullable String cleanText(@Nullable String txt) {
        if (txt == null) {
            return null;
        }
        return SPECIAL_CHARS_REGEX.matcher(txt).replaceAll("");
    }

    public static @Nullable String cleanIdentifier(@Nullable String param) {
        if (param == null) {
            return null;
        }
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < param.length(); ++i) {
            char c = param.charAt(i);
            if (!Character.isJavaIdentifierPart(c)) continue;
            paramBuilder.append(c);
        }
        return paramBuilder.toString();
    }

    public static String random(int count) {
        return StringUtils.random(count, RandomEnum.ALL);
    }

    public static String random(int count, RandomEnum randomEnum) {
        if (count == 0) {
            return "";
        }
        Assert.isTrue((count > 0 ? 1 : 0) != 0, (String)("Requested random string length " + count + " is less than 0."));
        SecureRandom random = RandomHolder.SECURE_RANDOM;
        char[] buffer = new char[count];
        for (int i = 0; i < count; ++i) {
            String factor = randomEnum.getDesc();
            buffer[i] = factor.charAt(random.nextInt(factor.length()));
        }
        return new String(buffer);
    }

    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        }
        char[] buf = new char[repeat];
        Arrays.fill(buf, ch);
        return new String(buf);
    }

    public static @Nullable String left(@Nullable String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static @Nullable String right(@Nullable String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        int length = str.length();
        if (length <= len) {
            return str;
        }
        return str.substring(length - len);
    }

    public static @Nullable String rightPad(@Nullable String str, int size) {
        return StringUtils.rightPad(str, size, ' ');
    }

    public static @Nullable String rightPad(@Nullable String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return StringUtils.rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(StringUtils.repeat(padChar, pads));
    }

    public static @Nullable String rightPad(@Nullable String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (!StringUtils.hasLength(padStr)) {
            padStr = BLANK;
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return StringUtils.rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; ++i) {
            padding[i] = padChars[i % padLen];
        }
        return str.concat(new String(padding));
    }

    public static @Nullable String leftPad(@Nullable String str, int size) {
        return StringUtils.leftPad(str, size, ' ');
    }

    public static @Nullable String leftPad(@Nullable String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return StringUtils.leftPad(str, size, String.valueOf(padChar));
        }
        return StringUtils.repeat(padChar, pads).concat(str);
    }

    public static @Nullable String leftPad(@Nullable String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (!org.springframework.util.StringUtils.hasLength((String)padStr)) {
            padStr = BLANK;
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return StringUtils.leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; ++i) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }

    public static @Nullable String mid(@Nullable String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (len < 0 || pos > length) {
            return "";
        }
        if (pos < 0) {
            pos = 0;
        }
        if (length <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    public static boolean isHttpUrl(String text) {
        return text.startsWith("http://") || text.startsWith("https://");
    }

    public static String nullToEmpty(Object str) {
        return str != null ? str.toString() : "";
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String subString2(String str, int maxlen) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return str.substring(0, maxlen);
    }

    public static String subString3(String str, int maxlen) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return str.substring(0, maxlen) + "...";
    }

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasText(CharSequence str) {
        if (!StringUtils.hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return true;
        }
        return false;
    }

    public static boolean hasText(String str) {
        return StringUtils.hasText((CharSequence)str);
    }

    public static String trimToNull(String nextLine) {
        return nextLine.trim();
    }

    public static String appendIfNotContain(String str, String appendStr, String otherwise) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(appendStr)) {
            return str;
        }
        if (str.contains(appendStr)) {
            return str.concat(otherwise);
        }
        return str.concat(appendStr);
    }

    public static String filterSpecialChart(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\uff01@#\uffe5%\u2026\u2026&*\uff08\uff09\u2014\u2014+|{}\u3010\u3011\u2018\uff1b\uff1a\u201d\u201c\u2019\u3002\uff0c\u3001\uff1f]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static Pair<String, String> getPropertyPair(String text) {
        if (StringUtils.isEmpty(text)) {
            return Pair.of(null, null);
        }
        int index = text.indexOf("=");
        if (index < 0) {
            return Pair.of(text, null);
        }
        String key = text.substring(0, index);
        String value = text.substring(index + 1);
        return Pair.of(key, value);
    }

    public static void ifBlank(CharSequence str, Consumer<CharSequence> consumer) {
        if (StrUtil.isBlank((CharSequence)str)) {
            consumer.accept(str);
        }
    }

    public static void ifNotBlank(CharSequence str, Consumer<CharSequence> consumer) {
        if (StrUtil.isNotBlank((CharSequence)str)) {
            consumer.accept(str);
        }
    }

    public static void ifEmpty(CharSequence str, Consumer<CharSequence> consumer) {
        if (StrUtil.isEmpty((CharSequence)str)) {
            consumer.accept(str);
        }
    }

    public static void ifNotEmpty(CharSequence str, Consumer<CharSequence> consumer) {
        if (StrUtil.isNotEmpty((CharSequence)str)) {
            consumer.accept(str);
        }
    }
}

