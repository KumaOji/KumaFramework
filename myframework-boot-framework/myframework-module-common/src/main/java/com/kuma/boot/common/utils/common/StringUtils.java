/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.common;

import static com.kuma.boot.common.constant.CommonConstants.BACK_SLASH;
import static com.kuma.boot.common.constant.CommonConstants.DOUBLE_QUOTES;
import static com.kuma.boot.common.constant.CommonConstants.UTF8;
import static com.kuma.boot.common.utils.collection.ArrayUtils.getEndIndex;
import static com.kuma.boot.common.utils.collection.CollectionUtils.difference;
import static com.kuma.boot.common.utils.common.ArrayPrimitiveUtils.getPreChar;
import static com.kuma.boot.common.utils.date.DateUtils.PURE_DATE_FORMAT;
import static com.kuma.boot.common.utils.date.DateUtils.getDateFormat;
import static com.kuma.boot.common.utils.date.DateUtils.getFormatDate;
import static com.kuma.boot.common.utils.reflect.ClassTypeUtils.isArray;

import com.kuma.boot.common.constant.PunctuationConstants;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.condition.Condition;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
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

/**
 * 字符串工具类
 *
 * @author bbhou
 * @since 0.0.1
 */
public final class StringUtils {

    /**
     * 大写的字母
     *
     * @since 0.1.66
     */
    public static final String LETTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWSXYZ";

    /**
     * 小写的字母
     *
     * @since 0.1.66
     */
    public static final String LETTERS_LOWER = "abcdefghijklmnopqrstuvwsxyz";

    /**
     * 空白信息的表达式
     *
     * @since 0.1.98
     */
    private static final Pattern BLANK_PATTERN = Pattern.compile("\\s*|\t|\r|\n");

    private StringUtils() {}

    /**
     * 空值变量
     */
    public static final String Empty = "";

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 空 json
     *
     * @since 0.1.80
     */
    public static final String EMPTY_JSON = "{}";

    /**
     * 空格
     */
    public static final String BLANK = " ";

    /**
     * 新行
     * <p>
     * System.lineSeparator() 实际的文本效果是2行
     *
     * @since 0.1.129
     */
    public static final String NEW_LINE = "";

    /**
     * 是否不为换行符
     * @param line 内容
     * @return 是否
     * @since 0.1.129
     */
    public static boolean isNotReturnLine(String line) {
        return !isReturnLine(line);
    }

    /**
     * 验证是否为空
     * @param obj 数据
     * @return 返回布尔值
     */
    public static boolean isNullAndSpaceOrEmpty(Object obj) {
        // 验证是否为 null
        if (obj == null) {
            return true;
        }
        // 验证是否为空
        return Empty.equals(obj.toString().trim());
        // 返回结果
    }

    /**
     * 是否为换行符
     * @param line 内容
     * @return 是否
     * @since 0.1.129
     */
    public static boolean isReturnLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return true;
        }

        String trim = line.trim();
        if (StringUtils.isEmpty(trim)) {
            return true;
        }

        if (NEW_LINE.equals(line)) {
            return true;
        }

        return false;
    }

    /**
     * 是否全部为大写
     * @param string 待检验字符
     * @return 是否为大写
     */
    public static boolean isUpperCase(final String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }

        char[] characters = string.toCharArray();
        for (char c : characters) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否全部为小写
     * @param string 待检验字符
     * @return 是否为大写
     */
    public static boolean isLowerCase(final String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }

        char[] characters = string.toCharArray();
        for (char c : characters) {
            if (!Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含大写字母
     * @param string 待检验字符
     * @return 是否为大写
     */
    public static boolean containsUppercase(final String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }

        char[] characters = string.toCharArray();
        for (char c : characters) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含小写字母
     * @param string 待检验字符
     * @return 是否为大写
     */
    public static boolean containsLowercase(final String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }

        char[] characters = string.toCharArray();
        for (char c : characters) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否全部由字母组成 1. 大写字母 2. 小写字母
     * @param string 字符串
     * @return 结果
     * @since 0.1.68
     */
    public static boolean isLetter(final String string) {
        return isCharsCondition(
                string,
                new Condition<Character>() {
                    @Override
                    public boolean condition(Character character) {
                        return Character.isLowerCase(character) || Character.isUpperCase(character);
                    }
                });
    }

    /**
     * 是否全部为数字
     * @param string 字符串
     * @return 是否为数字
     * @since 0.1.68
     */
    public static boolean isDigit(final String string) {
        return isCharsCondition(
                string,
                new Condition<Character>() {
                    @Override
                    public boolean condition(Character character) {
                        return Character.isDigit(character);
                    }
                });
    }

    /**
     * 是否全部为数字或者字母
     * @param string 字符串
     * @return 是否数字或者字母
     * @since 0.1.68
     */
    public static boolean isDigitOrLetter(final String string) {
        return isCharsCondition(
                string,
                new Condition<Character>() {
                    @Override
                    public boolean condition(Character character) {
                        return CharUtils.isDigitOrLetter(character);
                    }
                });
    }

    /**
     * 是否全部为数组
     * @param string 字符串
     * @return 结果
     * @since 0.1.157
     */
    public static boolean isNumber(final String string) {
        return isCharsCondition(
                string,
                new Condition<Character>() {
                    @Override
                    public boolean condition(Character character) {
                        return CharUtils.isNumber(character);
                    }
                });
    }

    /**
     * 字符串是否全部满足某一个条件
     * @param string 原始字符串
     * @param condition 条件
     * @return 是否满足
     * @since 0.1.68
     */
    private static boolean isCharsCondition(
            final String string, final Condition<Character> condition) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }

        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (!condition.condition(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为空
     * @param string 字符串
     * @return {@code true} 为空
     */
    public static boolean isEmpty(final String string) {
        return null == string || EMPTY.equals(string);
    }

    /**
     * 是否为空-进行 trim 之后
     * @param string 原始字符串
     * @return 是否
     * @since 0.1.71
     */
    public static boolean isEmptyTrim(final String string) {
        if (isEmpty(string)) {
            return true;
        }

        String trim = trim(string);
        return isEmpty(trim);
    }

    /**
     * 是否不为空-进行 trim 之后
     * @param string 原始字符串
     * @return 是否
     * @since 0.1.102
     */
    public static boolean isNotEmptyTrim(final String string) {
        return !isEmptyTrim(string);
    }

    /**
     * 是否为空的 json
     * @param json json 信息
     * @return 是否
     * @since 0.1.80
     */
    public static boolean isEmptyJson(final String json) {
        if (isEmptyTrim(json)) {
            return true;
        }

        String trim = json.trim();
        return EMPTY_JSON.equals(trim);
    }

    /**
     * 是否为非空
     * @param string 字符串
     * @return {@code true} 为非空
     */
    public static boolean isNotEmpty(final String string) {
        return !isEmpty(string);
    }

    /**
     * 是否为空
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * 是否不为空
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 根据任意多的空格进行分割字符串。 1. 入参为空,则返回空字符串数组
     * @param string 字符串
     * @return 割字符串数组
     */
    public static String[] splitByAnyBlank(final String string) {
        if (StringUtils.isEmpty(string)) {
            return new String[0];
        }

        final String pattern = "\\s+|\u0013";
        return string.split(pattern);
    }

    /**
     * 过滤掉所有的空格 （1）trim （2）移除所有的空格
     * @param string 原始字符串
     * @return 过滤后的内容
     * @since 0.1.68
     */
    public static String trimAnyBlank(final String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        String trim = string.trim();
        return trim.replaceAll("\\s+|\u0013", "");
    }

    /**
     * 替换掉任意空格
     * @param string 原始字符串
     * @param replacement 待替换的文本
     * @return 结果
     * @since 0.1.98
     */
    public static String replaceAnyBlank(final String string, final String replacement) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        Matcher m = BLANK_PATTERN.matcher(string);
        String result = m.replaceAll(replacement);
        // 160 &nbsp;
        result = result.replaceAll("\\u00A0", replacement);
        return result;
    }

    /**
     * 替换掉任意空格为空
     * @param string 原始字符串
     * @return 结果
     * @since 0.1.98
     */
    public static String replaceAnyBlank(final String string) {
        return replaceAnyBlank(string, StringUtils.EMPTY);
    }

    /**
     * 过滤掉所有的标点符号 （1）trim （2）移除标点符号 （3）移除 symbol
     * @param string 原始字符串
     * @return 过滤后的内容
     * @since 0.1.68
     */
    public static String trimAnyPunctionAndSymbol(final String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        String trim = string.trim();
        return trim.replaceAll("\\p{P}|\\p{S}", "");
    }

    /**
     * 获取的驼峰写法。 1.这是 mybatis-gen 源码
     * @param inputString 输入字符串
     * @param firstCharacterUppercase 首字母是否大写。
     * @return 驼峰写法
     */
    public static String getCamelCaseString(String inputString, boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;

                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                    break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * 首字母小写
     * @param str 字符串
     * @return 首字母小写字符串
     */
    public static String firstToLowerCase(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 首字母大写
     * @param str 字符串
     * @return 首字母大写结果
     */
    public static String firstToUpperCase(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 默认为 “” 1. 如果为 null TO "" 2. 返回本身
     * @param string 字符串
     * @return 非 null 的字符串
     */
    public static String defaultEmpty(final String string) {
        if (isEmpty(string)) {
            return EMPTY;
        }
        return string;
    }

    /**
     * 将数组进行逗号连接
     * @param array object array
     * @return join string
     * @since 0.1.46
     */
    public static String join(Object... array) {
        return join(array, PunctuationConstants.COMMA);
    }

    /**
     * 将数组进行连接
     * @param array object array
     * @param separator 分隔符
     * @return join string
     * @see #join(Object[], String, int, int) 核心实现
     * @since 0.1.14
     */
    public static String join(Object[] array, String separator) {
        final int endIndex = getEndIndex(-1, array);
        return join(array, separator, 0, endIndex);
    }

    /**
     * 拼接
     * @param splitter 拼接符
     * @param objects 结果
     * @return 结果
     * @since 0.1.153
     */
    public static String join(String splitter, Object... objects) {
        return join(objects, splitter);
    }

    /**
     * 将数组进行连接 from: apache lang3
     * @param array object array
     * @param separator 分隔符
     * @param startIndex 开始下标
     * @param endIndex 结束下标
     * @return join string
     */
    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }

        if (separator == null) {
            separator = "";
        }

        int noOfItems = endIndex - startIndex;
        if (noOfItems < 0) {
            return "";
        } else {
            StringBuilder buf = new StringBuilder(noOfItems * 16);

            for (int i = startIndex; i <= endIndex; ++i) {
                if (i > startIndex) {
                    buf.append(separator);
                }

                if (array[i] != null) {
                    buf.append(array[i]);
                }
            }

            return buf.toString();
        }
    }

    /**
     * 字符串拼接 (1) v0.1.14 将其范围扩展到对象列表 注意：如果有 null 属性，会导致直接报错。此处不再处理。
     * @param collection 集合列表
     * @param splitter 分隔符
     * @param startIndex 开始下标
     * @param endIndex 结束下标
     * @param <E> 泛型
     * @return 结果
     * @since 0.1.14
     */
    public static <E> String join(
            final Collection<E> collection,
            final String splitter,
            final int startIndex,
            final int endIndex) {
        if (CollectionUtil.isEmpty(collection)) {
            return StringUtils.EMPTY;
        }

        final String actualSplitter = StringUtils.nullToDefault(splitter, StringUtils.EMPTY);
        StringBuilder stringBuilder = new StringBuilder();

        Iterator<E> iterator = collection.iterator();
        // 循环直到 startIndex
        for (int i = 0; i < startIndex; i++) {
            iterator.next();
        }
        stringBuilder.append(iterator.next().toString());
        for (int i = startIndex; i < endIndex; i++) {
            stringBuilder.append(actualSplitter).append(iterator.next().toString());
        }
        return stringBuilder.toString();
    }

    // /**
    // * 字符串拼接 (1) v0.1.14 将其范围扩展到对象列表 注意：如果有 null 属性，会导致直接报错。此处不再处理。
    // *
    // * @param collection 集合信息
    // * @param splitter 分隔符
    // * @param <E> 泛型
    // * @return 结果
    // * @since 0.1.14
    // */
    // public static <E> String join(final Collection<E> collection, final String
    // splitter) {
    // final int endIndex = CollectionUtil.getEndIndex(-1, collection);
    // return join(collection, splitter, 0, endIndex);
    // }

    /**
     * 字符串按逗号拼接拼接
     * @param collection 集合信息
     * @param <E> 泛型
     * @return 结果
     * @since 0.1.46
     */
    public static <E> String join(final Collection<E> collection) {
        return join(collection, PunctuationConstants.COMMA);
    }

    /**
     * 驼峰命名转下划线
     * @param camelStr 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToUnderline(String camelStr) {
        if (StringUtils.isEmpty(camelStr)) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        char[] chars = camelStr.toCharArray();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 下划线转驼峰命名
     * @param underlineStr 下划线字符串
     * @return 驼峰字符串
     */
    public static String underlineToCamel(String underlineStr) {
        if (StringUtils.isEmpty(underlineStr)) {
            return StringUtils.EMPTY;
        }

        int len = underlineStr.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = underlineStr.charAt(i);
            if (c == '_') {
                if (++i < len) {
                    sb.append(Character.toUpperCase(underlineStr.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 重复多少次
     * @param component 组成信息
     * @param times 重复次数
     * @return 重复多次的字符串结果
     */
    public static String repeat(final String component, final int times) {
        if (StringUtils.isEmpty(component) || times <= 0) {
            return StringUtils.EMPTY;
        }

        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuffer.append(component);
        }

        return stringBuffer.toString();
    }

    /**
     * 构建新的字符串
     * @param original 原始对象
     * @param middle 中间隐藏信息
     * @param prefixLength 前边信息长度
     * @return 构建后的新字符串
     * @since 0.0.8
     */
    public static String buildString(
            final Object original, final String middle, final int prefixLength) {
        if (ObjectUtils.isNull(original)) {
            return null;
        }

        final String string = original.toString();
        final int stringLength = string.length();

        String prefix = "";
        String suffix = "";

        if (stringLength >= prefixLength) {
            prefix = string.substring(0, prefixLength);
        } else {
            prefix = string.substring(0, stringLength);
        }

        int suffixLength = stringLength - prefix.length() - middle.length();
        if (suffixLength > 0) {
            suffix = string.substring(stringLength - suffixLength);
        }

        return prefix + middle + suffix;
    }

    /**
     * 过滤掉空格
     * @param original 原始字符串
     * @return 过滤后的字符串
     * @since 0.1.0
     */
    public static String trim(final String original) {
        if (StringUtils.isEmpty(original)) {
            return original;
        }
        return original.trim();
    }

    /**
     * 如果字符串是<code>null</code>，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * nullToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * nullToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * nullToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * nullToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     * @param str 要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     * @since 0.1.0
     */
    public static String nullToDefault(CharSequence str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串
     * @param str 被填充的字符串
     * @param filledChar 填充的字符
     * @param len 填充长度
     * @param isPre 是否填充在前
     * @return 填充后的字符串
     * @since 0.1.0
     */
    public static String fill(String str, char filledChar, int len, boolean isPre) {
        final int strLen = str.length();
        if (strLen > len) {
            return str;
        }

        String filledStr = StringUtils.repeat(String.valueOf(filledChar), len - strLen);
        return isPre ? filledStr.concat(str) : str.concat(filledStr);
    }

    /**
     * 对象转换为字符串 1. 对数组特殊处理 {@link Arrays#toString(Object[])} 避免打印无意义的信息（v0.1.14）
     * @param object 对象
     * @param defaultWhenNull 对象为空时的默认值
     * @return 结果
     * @since 0.1.5
     */
    public static String objectToString(final Object object, final String defaultWhenNull) {
        if (ObjectUtils.isNull(object)) {
            return defaultWhenNull;
        }
        Class<?> type = object.getClass();
        if (isArray(type)) {
            Object[] arrays = (Object[]) object;
            return Arrays.toString(arrays);
        }
        return object.toString();
    }

    /**
     * 对象转换为字符串 1. 默认为空时返回 null
     * @param object 对象
     * @return 结果
     * @since 0.1.5
     */
    public static String objectToString(final Object object) {
        return objectToString(object, null);
    }

    /**
     * 对 single 的信息重复多次
     * @param single 单个字符
     * @param times 重复次数
     * @return 结果
     * @see #repeat(String, int) 重复
     * @since 0.1.9
     */
    @Deprecated
    public static String times(final String single, final int times) {
        if (StringUtils.isEmpty(single)) {
            return single;
        }
        if (times <= 0) {
            return single;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(single);
        }
        return stringBuilder.toString();
    }

    /**
     * 首字母大写
     * @param string 字符串
     * @return 大写的结果
     * @since 0.1.11
     */
    public static String capitalFirst(final String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        if (string.length() <= 1) {
            return string.toUpperCase();
        }

        char capitalChar = Character.toUpperCase(string.charAt(0));
        return capitalChar + string.substring(1);
    }

    /**
     * 严格拆分 【传统拆分】 1:2:3:31::32:4 结果是：[1, 2, 3, 31, , 32, 4]
     * <p>
     * 【严格拆分】 严格匹配 : 拆分符，如果有多个，则不进行拆分。 结果：[1, 2, 3, 31::32, 4]
     * <p>
     * 实现逻辑： （1）根据 index 获取所有的下标。+length（当前步长） （2）获取当前的所有拆分下标，获取 times+1 的拆分下标 （3）从当前下标中移除
     * times+1 的下标。并且移除连续的信息。 连续：times+1 的下标，后续的 times 步长。如果不连续，则中断。 （4）根据过滤后的列表生成最后的结果。
     * @param string 原始字符串
     * @param splitUnit 分隔单元
     * @param times 次数
     * @return 结果
     * @since 0.1.16
     */
    public static List<String> splitStrictly(
            final String string, final char splitUnit, final int times) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (times <= 0) {
            return Collections.singletonList(string);
        }

        // 分别获取索引列表
        final String split = CharUtils.repeat(splitUnit, times);
        final String moreSplit = CharUtils.repeat(splitUnit, times + 1);
        final List<Integer> splitIndexList = getIndexList(string, split);
        final List<Integer> moreSplitIndexList = getIndexList(string, moreSplit);

        // 移除重复下标
        final List<Integer> removeIndexList =
                getSerialFilterList(splitIndexList, moreSplitIndexList, times);

        // 构建结果列表
        Collection<Integer> trimIndexList = difference(splitIndexList, removeIndexList);
        return subStringList(string, trimIndexList, times);
    }

    /**
     * 获取满足条件连续的列表 （1）当前信息 （2）连续的索引信息
     * @param allList 所有的整数
     * @param filterList 待排除的整数
     * @param step 步长
     * @return 结果列表
     */
    private static List<Integer> getSerialFilterList(
            final List<Integer> allList, final List<Integer> filterList, final int step) {

        List<Integer> resultList = new ArrayList<>(filterList);
        // 根据 index+times 为步长进行连续判断。不存在则跳过
        for (Integer filter : filterList) {
            // 从匹配的下一个元素开始
            final int startIndex = allList.indexOf(filter) + 1;
            int stepTimes = 1;
            for (int i = startIndex; i < allList.size(); i++) {
                final Integer indexVal = allList.get(i);
                final int nextVal = step * stepTimes + filter;
                if (indexVal.equals(nextVal)) {
                    resultList.add(nextVal);
                } else {
                    // 跳出当前循环
                    break;
                }
                stepTimes++;
            }
        }

        return resultList;
    }

    /**
     * 根据下标截取列表
     * <p>
     * 【最后的截取问题】 最后构建的结果： string=1::2::3:31:::32::4: index=[1,4,15] ignore=2
     * <p>
     * 每次截取： [0,1) [1+2,4) [15+2,]
     * @param string 原始字符串
     * @param indexCollection 下标列表
     * @param ignoreLength 每次忽略跳过的长度。用于跳过 split 字符。
     * @return 结果列表
     * @since 0.1.16
     */
    public static List<String> subStringList(
            final String string,
            final Collection<Integer> indexCollection,
            final int ignoreLength) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(indexCollection)) {
            return Collections.singletonList(string);
        }

        List<String> resultList = new ArrayList<>();
        int startIndex = 0;
        for (Integer index : indexCollection) {
            // 最后的位置添加空字符串
            if (startIndex > string.length() - 1) {
                resultList.add(StringUtils.EMPTY);
                break;
            }
            String subString = string.substring(startIndex, index);
            resultList.add(subString);
            startIndex = index + ignoreLength;
        }
        // 最后的结果信息
        if (startIndex < string.length()) {
            String subString = string.substring(startIndex);
            resultList.add(subString);
        }

        return resultList;
    }

    /**
     * 获取所有符合条件的下标类表 【下标】 1:2:3:31::32:4:
     * <p>
     * [1, 3, 5, 8, 9, 12, 14]
     * <p>
     * 问题：这个下标没有过滤 split。 如果想过滤分隔符，应该如下： (0,1) (1+split.length, 3) ... 1,2,
     * @param string 原始字符串
     * @param split 分隔字符串
     * @return 下标列表
     * @since 0.1.16
     */
    public static List<Integer> getIndexList(final String string, final String split) {
        if (StringUtils.isEmpty(string) || StringUtils.isEmpty(split)) {
            return Collections.emptyList();
        }

        List<Integer> indexList = new ArrayList<>();
        int startIndex = 0;
        while (startIndex < string.length()) {
            startIndex = string.indexOf(split, startIndex);
            if (startIndex < 0) {
                break;
            }
            indexList.add(startIndex);
            startIndex += split.length();
        }
        return indexList;
    }

    /**
     * 获取字符串对应的下标信息
     * @param string 字符串
     * @param symbol 分隔符
     * @param ignoreDoubleQuotes 是否忽略双引号中的信息
     * @return 结果列表
     * @since 0.1.27
     */
    public static List<Integer> getIndexList(
            final String string, final char symbol, final boolean ignoreDoubleQuotes) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }

        List<Integer> resultList = new ArrayList<>();
        char[] chars = string.toCharArray();

        boolean doubleQuotesStart = false;
        char preChar = ' ';
        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];

            preChar = getPreChar(preChar, currentChar);
            // 上一个字符不是转义，且当前为 "。则进行状态的切换
            if (BACK_SLASH != preChar && DOUBLE_QUOTES == currentChar) {
                doubleQuotesStart = !doubleQuotesStart;
            }

            // 等于且不在双引号中。
            if (currentChar == symbol) {
                // 忽略双引号中的信息 && 不在双引号中。
                if (ignoreDoubleQuotes) {
                    if (!doubleQuotesStart) {
                        resultList.add(i);
                    }
                } else {
                    resultList.add(i);
                }
            }
        }
        return resultList;
    }

    // /**
    // * 获取上一个字符
    // * <p>
    // * 保证转义字符的两次抵消。
    // *
    // * @param preChar 上一个字符
    // * @param currentChar 当前字符
    // * @return 结果
    // * @since 0.1.27
    // */
    // @Deprecated
    // private static char getPreChar(final char preChar, final char currentChar) {
    // // 判断前一个字符是什么
    // if (BACK_SLASH == preChar
    // && BACK_SLASH == currentChar) {
    // return BLANK;
    // }
    // return currentChar;
    // }

    /**
     * 根据索引下标直接拆分
     * @param string 原始字符串
     * @param indexList 结果列表
     * @return 结果
     * @since 0.1.27
     */
    public static List<String> splitByIndexes(final String string, final List<Integer> indexList) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(indexList)) {
            return Collections.singletonList(string);
        }

        List<String> resultList = new ArrayList<>();

        int preIndex = 0;
        for (Integer anIndexList : indexList) {
            int currentIndex = anIndexList;
            if (currentIndex > preIndex) {
                resultList.add(string.substring(preIndex, currentIndex));
            }
            preIndex = currentIndex + 1;
        }
        // 判断最后一个下标
        final int lastIndex = indexList.get(indexList.size() - 1);
        if (lastIndex + 1 < string.length()) {
            resultList.add(string.substring(lastIndex + 1));
        }
        return resultList;
    }

    /**
     * 字符串转字节数组
     * @param string 字符串
     * @return 字节数组
     * @since 0.1.35
     */
    public static byte[] stringToBytes(final String string) {
        if (ObjectUtils.isNull(string)) {
            return null;
        }

        return string.getBytes();
    }

    /**
     * 字节数组转字符串
     * @param bytes 字节数组
     * @return 字符串
     * @since 0.1.35
     */
    public static String bytesToString(final byte[] bytes) {
        if (ArrayPrimitiveUtils.isEmpty(bytes)) {
            return null;
        }

        return new String(bytes);
    }

    /**
     * 拆分为字符串数组
     * @param string 字符串
     * @param splitter 拆分符号
     * @return 字符串数组
     * @since 0.1.46
     */
    public static String[] splitToStringArray(final String string, final String splitter) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }

        return string.split(splitter);
    }

    /**
     * 拆分为字符串数组
     * @param string 字符串
     * @return 字符串数组
     * @since 0.1.46
     */
    public static String[] splitToStringArray(final String string) {
        return splitToStringArray(string, PunctuationConstants.COMMA);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final byte[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Byte> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        new Handler<Byte, Byte>() {
                            @Override
                            public Byte handle(Byte value) {
                                return value;
                            }
                        });
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final char[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Character> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        new Handler<Character, Character>() {
                            @Override
                            public Character handle(Character value) {
                                return value;
                            }
                        });
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final short[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Short> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        value -> value);
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final long[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Long> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        value -> value);
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final float[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Float> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        value -> value);
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final double[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Double> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        new Handler<Double, Double>() {
                            @Override
                            public Double handle(Double value) {
                                return value;
                            }
                        });
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final boolean[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Boolean> lists =
                ArrayPrimitiveUtils.toList(
                        array,
                        new Handler<Boolean, Boolean>() {
                            @Override
                            public Boolean handle(Boolean value) {
                                return value;
                            }
                        });
        return join(lists, splitter);
    }

    /**
     * 数组拼接为字符串
     * @param array 数组
     * @param splitters 分隔符
     * @return 拼接结果
     * @since 0.1.49
     */
    public static String join(final int[] array, final String... splitters) {
        if (ArrayPrimitiveUtils.isEmpty(array)) {
            return StringUtils.EMPTY;
        }

        String splitter = getSplitter(splitters);
        List<Integer> integers =
                ArrayPrimitiveUtils.toList(
                        array,
                        new Handler<Integer, Integer>() {
                            @Override
                            public Integer handle(Integer integer) {
                                return integer;
                            }
                        });
        return join(integers, splitter);
    }

    /**
     * 获取指定的分隔符
     * @param splitters 分隔符
     * @return 字符串
     * @since 0.1.49
     */
    private static String getSplitter(final String... splitters) {
        if (ArrayUtils.isEmpty(splitters)) {
            return PunctuationConstants.COMMA;
        }

        return splitters[0];
    }

    /**
     * 拆分为列表
     * @param string 字符串
     * @param splitter 分隔符号
     * @return 字符串列表
     * @since 0.1.49
     */
    public static List<String> splitToList(final String string, final String splitter) {
        ArgUtils.notEmpty(splitter, "splitter");

        if (StringUtils.isEmpty(string)) {
            return new ArrayList<>();
        }

        String[] strings = string.split(splitter);
        return ArrayUtils.toList(strings);
    }

    /**
     * 拆分为列表
     * @param string 字符串
     * @param c 字符
     * @return 结果
     * @since 0.2.3
     */
    public static List<String> splitToList(final String string, final char c) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }

        char[] chars = string.toCharArray();
        return splitToList(chars, c);
    }

    /**
     * 拆分为列表
     * @param chars 字符串
     * @param c 字符
     * @return 结果
     * @since 0.2.3
     */
    public static List<String> splitToList(char[] chars, final char c) {
        if (ArrayPrimitiveUtils.isEmpty(chars)) {
            return Collections.emptyList();
        }

        List<String> resultList = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        for (char cs : chars) {
            // 符合拆分条件
            if (c == cs) {
                resultList.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(cs);
            }
        }

        // 如果还有
        if (!stringBuilder.isEmpty()) {
            resultList.add(stringBuilder.toString());
        }

        return resultList;
    }

    /**
     * 拆分为列表
     * @param string 字符串
     * @return 字符串列表
     * @since 0.1.49
     */
    public static List<String> splitToList(final String string) {
        return splitToList(string, PunctuationConstants.COMMA);
    }

    /**
     * 转换为数组字符
     * @param string 字符串
     * @return 结果
     * @since 0.1.66
     */
    public static Character[] toCharacterArray(final String string) {
        final char[] chars = string.toCharArray();
        Character[] newArray = new Character[chars.length];

        for (int i = 0; i < chars.length; i++) {
            newArray[i] = chars[i];
        }

        return newArray;
    }

    /**
     * 转换为列表字符
     * @param string 字符串
     * @return 结果
     * @since 0.1.66
     */
    public static List<Character> toCharacterList(final String string) {
        final char[] chars = string.toCharArray();
        List<Character> newList = new ArrayList<>(chars.length);

        for (char aChar : chars) {
            newList.add(aChar);
        }

        return newList;
    }

    /**
     * 转换为 char 字符串列表
     * @param string 字符串
     * @return 字符串列表
     * @since 0.1.74
     */
    public static List<String> toCharStringList(final String string) {
        if (StringUtils.isEmpty(string)) {
            return new ArrayList<>();
        }

        char[] chars = string.toCharArray();
        return ArrayPrimitiveUtils.toList(
                chars,
                new Handler<Character, String>() {
                    @Override
                    public String handle(Character character) {
                        return String.valueOf(character);
                    }
                });
    }

    /**
     * 转换为 char set
     * @param string 字符串
     * @return 字符串列表
     * @since 0.6.0
     */
    public static Set<Character> toCharSet(final String string) {
        if (StringUtils.isEmpty(string)) {
            return new HashSet<>();
        }

        char[] chars = string.toCharArray();
        Set<Character> set = new HashSet<>();
        for (char c : chars) {
            set.add(c);
        }

        return set;
    }

    /**
     * 将字符串中的全角字符转为半角
     * @param string 字符串
     * @return 转换之后的字符串
     * @since 0.1.68
     */
    public static String toHalfWidth(final String string) {
        return characterHandler(
                string,
                new Handler<Character, Character>() {
                    @Override
                    public Character handle(Character character) {
                        return CharUtils.toHalfWidth(character);
                    }
                });
    }

    /**
     * 将字符串中的半角字符转为全角
     * @param string 字符串
     * @return 转换之后的字符串
     * @since 0.1.68
     */
    public static String toFullWidth(final String string) {
        return characterHandler(
                string,
                new Handler<Character, Character>() {
                    @Override
                    public Character handle(Character character) {
                        return CharUtils.toFullWidth(character);
                    }
                });
    }

    /**
     * 字符的处理
     * @param string 字符串
     * @param handler 处理类
     * @return 结果
     * @since 0.1.68
     */
    private static String characterHandler(
            final String string, final Handler<Character, Character> handler) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }

        // 1. 转换为列表
        char[] chars = string.toCharArray();
        char[] resultChars = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            resultChars[i] = handler.handle(chars[i]);
        }

        // 2. 构建结果
        return new String(resultChars);
    }

    /**
     * 过滤掉非中文字符
     * @param string 字符串
     * @return 结果
     * @since 0.1.79
     */
    public static String trimNotChinese(final String string) {
        if (StringUtils.isEmptyTrim(string)) {
            return StringUtils.EMPTY;
        }

        char[] chars = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (Character character : chars) {
            if (CharUtils.isChinese(character)) {
                stringBuilder.append(character);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 避免默认实现的问题
     * @param object 对象
     * @return 结果
     * @see String#valueOf(Object) 默认实现会把 null 转换为 "null"
     * @since 0.1.102
     */
    public static String valueOf(final Object object) {
        if (ObjectUtils.isNull(object)) {
            return null;
        }

        return String.valueOf(object);
    }

    /**
     * 左补信息
     * @param original 原始字符串
     * @param targetLength 目标长度
     * @param unit 补的元素
     * @return 结果
     * @since 0.1.104
     */
    public static String leftPadding(
            final String original, final int targetLength, final char unit) {
        ArgUtils.notNull(original, "original");

        // 1. fast-return
        final int originalLength = original.length();
        if (originalLength >= targetLength) {
            return original;
        }

        // 2. 循环补零
        StringBuilder stringBuilder = new StringBuilder(targetLength);
        for (int i = originalLength; i < targetLength; i++) {
            stringBuilder.append(unit);
        }
        stringBuilder.append(original);

        return stringBuilder.toString();
    }

    /**
     * 左补信息 默认左补零 0
     * @param original 原始字符串
     * @param targetLength 目标长度
     * @return 结果
     * @since 0.1.104
     */
    public static String leftPadding(final String original, final int targetLength) {
        return leftPadding(original, targetLength, '0');
    }

    /**
     * 获取第一个字符
     * @param text 文本
     * @return 结果
     * @since 0.1.122
     */
    public static Character getFirstChar(final String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        return text.charAt(0);
    }

    /**
     * 空转换为 null
     * @param text 文本
     * @return 结果
     * @since 0.1.123
     */
    public static String emptyToNull(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return text;
    }

    /**
     * 转换为 boolean 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Boolean toBool(final String text) {
        return "YES".equalsIgnoreCase(text)
                || "TRUE".equalsIgnoreCase(text)
                || "1".equalsIgnoreCase(text);
    }

    /**
     * 转换为 boolean 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Character toChar(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return text.charAt(0);
    }

    /**
     * 转换为 Byte 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Byte toByte(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return Byte.valueOf(text);
    }

    /**
     * 转换为 Short 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Short toShort(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return Short.valueOf(text);
    }

    /**
     * 转换为 Integer 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Integer toInt(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return Integer.valueOf(text);
    }

    /**
     * 转换为 Long 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Long toLong(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return Long.valueOf(text);
    }

    /**
     * 转换为 Float 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Float toFloat(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return Float.valueOf(text);
    }

    /**
     * 转换为 Float 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Double toDouble(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return Double.valueOf(text);
    }

    /**
     * 转换为 BigInteger 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static BigInteger toBigInteger(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return new BigInteger(text);
    }

    /**
     * 转换为 BigDecimal 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static BigDecimal toBigDecimal(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        return new BigDecimal(text);
    }

    /**
     * 转换为 Date 类型
     * @param text 文本
     * @param dateFormat 格式化
     * @return 结果
     * @since 0.1.124
     */
    public static Date toDate(final String text, final String dateFormat) {
        if (isEmpty(text)) {
            return null;
        }

        return getFormatDate(text, dateFormat);
    }

    /**
     * 转换为 BigDecimal 类型
     * @param text 文本
     * @return 结果
     * @since 0.1.124
     */
    public static Date toDate(final String text) {
        return toDate(text, PURE_DATE_FORMAT);
    }

    /**
     * 转换为字符串
     * @param date 日期
     * @param format 格式化
     * @return 结果
     * @since 0.1.124
     */
    public static String toString(Date date, String format) {
        return getDateFormat(date, format);
    }

    /**
     * 转换为字符串
     * @param date 日期
     * @return 结果
     * @since 0.1.124
     */
    public static String toString(Date date) {
        return toString(date, PURE_DATE_FORMAT);
    }

    /**
     * 转换为字符串
     * @param object 对象
     * @return 结果
     * @since 0.1.124
     */
    public static String toString(Object object) {
        if (null == object) {
            return null;
        }

        return object.toString();
    }

    /**
     * 转换为字符串
     * @param bytes 字节
     * @param charset 编码
     * @return 结果
     * @since 0.1.130
     */
    public static String toString(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw new BootException(e);
        }
    }

    /**
     * 转换为字符串
     * @param bytes 字节
     * @return 结果
     * @since 0.1.130
     */
    public static String toString(byte[] bytes) {
        return toString(bytes, UTF8);
    }

    /**
     * 转换为 bytes
     * @param text 文本
     * @param charset 编码
     * @return 结果
     * @since 0.1.130
     */
    public static byte[] getBytes(String text, String charset) {
        try {
            return text.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new BootException(e);
        }
    }

    /**
     * 转换为 bytes
     * @param text 文本
     * @return 结果
     * @since 0.1.130
     */
    public static byte[] getBytes(String text) {
        return getBytes(text, UTF8);
    }

    /**
     * 是否全部是英文
     * @param text 文本
     * @return 结果
     * @since 0.1.132
     */
    public static boolean isEnglish(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (!CharUtils.isEnglish(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 是否全部是中文
     * @param text 文本
     * @return 结果
     * @since 0.1.132
     */
    public static boolean isChinese(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (!CharUtils.isChinese(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 包信息调整为路径信息
     * @param packageName 包信息
     * @return 結果
     * @since 0.1.141
     */
    public static String packageToPath(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return packageName;
        }

        return packageName.replaceAll("\\.", "/");
    }

    /**
     * 字符串截取
     * @param text 文本
     * @param startIndex 开始位置
     * @param length 长度
     * @return 结果
     * @since 0.1.142
     */
    public static String subString(String text, int startIndex, int length) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        // 长度
        if (length <= 0) {
            return null;
        }

        // 避免越界
        int endIndex = startIndex + length;
        if (endIndex > text.length()) {
            endIndex = text.length();
        }

        return text.substring(startIndex, endIndex);
    }

    /**
     * 在不同的操作系统中，对换号符的定义是不同的，比如：
     * <p>
     * 1. \n unix,linux系统，好像新的mac也是这样的。
     * <p>
     * 2. \r 有的mac系统
     * <p>
     * 3. \r\n window系统。
     * <p>
     * 自己观察，你会发现规律，其实用一个正则表达式就可以满足： \r?\n
     * @param content 内容
     * @return 结果
     * @since 0.1.143
     */
    public static List<String> contentToLines(String content) {
        if (content == null) {
            return null;
        }

        // 根据换行符分割
        String[] strings = content.split("\\r?\\n");
        return ArrayUtils.toList(strings);
    }

    /**
     * 字符串按照换行符拼接为新的内容
     * @param lines 行
     * @return 结果
     * @since 0.1.143
     */
    public static String linesToContent(List<String> lines) {
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lines.size() - 1; i++) {
            stringBuilder.append(lines.get(i)).append(SystemUtils.getLineSeparator());
        }

        stringBuilder.append(lines.get(lines.size() - 1));

        return stringBuilder.toString();
    }

    /**
     * 根据长度进行文本截断
     * @param text 文本
     * @param limitSize 限制长度
     * @return 结果列表
     * @since 0.1.149
     */
    public static List<String> splitByLength(String text, int limitSize) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }

        final int totalLength = text.length();

        int times = totalLength / limitSize;
        if (totalLength % limitSize != 0) {
            times++;
        }

        List<String> resultList = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {
            int startIndex = i * limitSize;
            int endIndex = (i + 1) * limitSize;

            // 越界处理
            if (endIndex > totalLength) {
                endIndex = totalLength;
            }
            String subText = text.substring(startIndex, endIndex);
            resultList.add(subText);
        }

        return resultList;
    }

    /**
     * 获取所有子字符串 比如：abc 1: a b c 2: ab bc 3: abc
     * <p>
     * 最大公共子串： https://blog.csdn.net/xiaojimanman/article/details/38924981
     * @param text 文本
     * @param minStepLen 最小步长
     * @return 结果列表
     */
    public static List<String> getAllSubStrList(String text, int minStepLen) {
        ArgUtils.positive(minStepLen, "stepLen");

        if (StringUtils.isEmpty(text) || minStepLen > text.length()) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();

        final int length = text.length();
        for (int i = minStepLen; i < length; i++) {
            // 从 0 开始，固定步长
            for (int j = 0; j <= length - i; j++) {
                String subStr = text.substring(j, j + i);
                list.add(subStr);
            }
        }

        return list;
    }

    /**
     * 替換掉 emoji
     * @param text 文本
     * @param replacement 替換的内容
     * @return 結果
     * @since 0.1.160
     */
    public static String replaceEmoji(String text, String replacement) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        return text.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", replacement);
    }

    /**
     * 替換掉 emoji
     * @param text 文本
     * @return 結果
     * @since 0.1.160
     */
    public static String replaceEmoji(String text) {
        return replaceEmoji(text, "");
    }

    /**
     * 获取对应的集合
     * @param text 文本
     * @return 结果
     * @since 1.17.0
     */
    public static Set<Character> getCharSet(final String text) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptySet();
        }

        char[] chars = text.toCharArray();
        Set<Character> characterSet = new HashSet<Character>(chars.length);
        for (int i = 0; i < chars.length; i++) {
            characterSet.add(chars[i]);
        }

        return characterSet;
    }

    /**
     * 按照字节数的长度截断
     * @param text 文本
     * @param bytesLen 字节长度
     * @return 结果
     * @since 0.13.0
     */
    public static String subWithBytes(final String text, final int bytesLen, final String charset) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        try {
            // 长度校验
            byte[] bytes = text.getBytes(charset);

            // 长度判断
            if (bytes.length <= bytesLen) {
                return text;
            }

            return new String(bytes, 0, bytesLen, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照字节数的长度截断
     * @param text 文本
     * @param bytesLen 字节长度
     * @return 结果
     * @since 0.13.0
     */
    public static String subWithBytes(final String text, final int bytesLen) {
        return subWithBytes(text, bytesLen, "UTF-8");
    }

}
