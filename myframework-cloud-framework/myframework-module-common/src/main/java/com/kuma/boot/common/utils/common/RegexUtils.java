/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

import static java.util.regex.Pattern.compile;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 正则表达式工具类 */
public final class RegexUtils {

    private RegexUtils() {}

    /**
     * 验证UUID
     */
    public static final String REGEX_UUID =
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    /**
     * 验证正数字
     */
    public static final String REGEX_NUMBER = "^[+-]?[0-9]*$";

    /**
     * 匹配浮点数 ^[1-9]\d*$ //匹配正整数 ^-[1-9]\d*$ //匹配负整数 ^-?[1-9]\d*$ //匹配整数 ^[1-9]\d*|0$
     * //匹配非负整数（正整数 + 0） ^-[1-9]\d*|0$ //匹配非正整数（负整数 + 0） ^[1-9]\d*\.\d*|0\.\d*[1-9]\d*$
     * //匹配正浮点数 ^-([1-9]\d*\.\d*|0\.\d*[1-9]\d*)$ //匹配负浮点数
     * ^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$ //匹配浮点数
     * ^[1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0$ //匹配非负浮点数（正浮点数 + 0）
     * ^(-([1-9]\d*\.\d*|0\.\d*[1-9]\d*))|0?\.0+|0$ //匹配非正浮点数（负浮点数 + 0）
     */
    public static final String REGEX_FLOAT_POINT =
            "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";

    /**
     * 只能输入n位的数字
     */
    public static final String REGEX_MAX_LENGTH_NUMBER = "^\\d{n}$";

    /**
     * 只能输入至少n位的数字
     */
    public static final String REGEX_LEAST_LENGTH_NUMBER = "^\\d{n,}$";

    /**
     * 只能输入m~n位的数字
     */
    public static final String REGEX_SPECIFIED_LENGTH_NUMBER = "^\\d{m,n}$";

    /**
     * 只能输入零和非零开头的数字
     */
    public static final String REGEX_NON_ZREO_NUMBER = "^(0|[1-9][0-9]*)$";

    /**
     * 只能输入有两位小数的正实数
     */
    public static final String REGEX_TWO_DECIMAL_FRACTION_NUMBER = "^[0-9]+(.[0-9]{2})?$";

    /**
     * 只能输入有1~3位小数的正实数
     */
    public static final String REGEX_SPECIFIED_DECIMAL_FRACTION_NUMBER = "^[0-9]+(.[0-9]{2})?$";

    /**
     * 只能输入非零的正整数
     */
    public static final String REGEX_NONZERO_POSITIVE_INTEGERS_NUMBER = "^\\+?[1-9][0-9]*$";

    /**
     * 只能输入非零的负整数
     */
    public static final String REGEX_NONZERO_NEGATIVE_INTEGERS_NUMBER = "^\\-[1-9][]0-9*$";

    /**
     * 只能输入长度为3的字符
     */
    public static final String REGEX_THREE_LENGTH_CHARACTER = "^.{3}$";

    /**
     * 只能输入由26个英文字母组成的字符串
     */
    public static final String REGEX_ENGLISH_CHARACTER = "^[A-Za-z]+$";

    /**
     * 只能输入由26个大写英文字母组成的字符串
     */
    public static final String REGEX_CAPITALIZATION_ENGLISH_CHARACTER = "^[A-Z]+$";

    /**
     * 只能输入由26个小写英文字母组成的字符串
     */
    public static final String REGEX_LOWERCASE_ENGLISH_CHARACTER = "^[a-z]+$";

    /**
     * 只能输入由数字和26个英文字母组成的字符串
     */
    public static final String REGEX_ENGLISH_AND_NUMBER_CHARACTER = "^[A-Za-z0-9]+$";

    /**
     * 只能输入由数字、26个英文字母或者下划线组成的字符串
     */
    public static final String REGEX_NON_SPECIAL_CHARACTER = "^\\w+$";

    /**
     * 验证用户密码 正确格式为：以字母开头，长度在6~18之间，只能包含字符、数字和下划线;
     */
    public static final String REGEX_PASSWORD_CHARACTER = "^[a-zA-Z]\\w{5,17}$";

    /**
     * 只能输入汉字
     */
    public static final String REGEX_CHINESE_CHARACTER = "^[\u4e00-\u9fa5]{0,}$";

    /**
     * 验证Email地址
     */
    public static final String REGEX_EMAIL_CHARACTER =
            "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    /**
     * 验证InternetURL
     */
    public static final String REGEX_INTERNET_URL_CHARACTER =
            "^http://%28[/\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    /**
     * 验证电话号码
     * 正确格式为："XXX-XXXXXXX"、"XXXX-XXXXXXXX"、"XXX-XXXXXXX"、"XXX-XXXXXXXX"、"XXXXXXX"和"XXXXXXXX";
     */
    public static final String REGEX_PHONE_CHARACTER = "^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$";

    /**
     * 验证身份证号（15位或18位数字）
     */
    public static final String REGEX_ID_NUMBER_CHARACTER = "^\\d{15}|\\d{18}$";

    /**
     * IE
     */
    public static final String IE = "msie ([\\d.]+)";

    /**
     * edge ie
     */
    public static final String IE_EDGE = "edge ([\\d.]+)";

    /**
     * Microsoft Edge
     */
    public static final String MICROSOFT_EDGE = "edg/([\\d.]+)";

    /**
     * firefox
     */
    public static final String FIREFOX = "firefox/([\\d.]+)";

    /**
     * chrome
     */
    public static final String CHROME = "chrome/([\\d.]+)";

    /**
     * opera
     */
    public static final String OPERA = "opr/([\\d.]+)";

    /**
     * safari
     */
    public static final String SAFARI = "version/([\\d.]+).*safari";

    /** 特殊字符 */
    public static final String[] SPECIAL_CHARS = {
            "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"
    };

    /**
     * 标点符号正则
     *
     * <p>
     * P 其中的小写 p 是 property 的意思，表示 Unicode 属性，用于 Unicode 正表达式的前缀。
     *
     * <p>
     * 等价于：
     *
     * <pre>
     * Pattern.compile("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
     * </pre>
     *
     * 大写 P 表示 Unicode 字符集七个字符属性之一：标点字符。 其他六个是 L：字母； M：标记符号（一般不会单独出现）； Z：分隔符（比如空格、换行等）；
     * S：符号（比如数学符号、货币符号等）； N：数字（比如阿拉伯数字、罗马数字等）； C：其他字符
     *
     * <p>
     * 相关信息： http://www.unicode.org/reports/tr18/
     * http://www.unicode.org/Public/UNIDATA/UnicodeData.txt
     */
    public static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{P}");

    /** 字母-正则模式 */
    public static final Pattern LETTER_PATTERN = Pattern.compile("\\p{L}");

    /** 标记性-正则模式 */
    public static final Pattern MARKABLE_PATTERN = Pattern.compile("\\p{M}");

    /**
     * 分隔符-正则模式
     *
     * <p>
     * 空格、换行等
     */
    public static final Pattern DELIMITER_PATTERN = Pattern.compile("\\p{Z}");

    /**
     * 符号-正则模式
     *
     * <p>
     * 数学符号、货币符号
     */
    public static final Pattern SYMBOL_PATTERN = Pattern.compile("\\p{S}");

    /**
     * 数字-正则模式
     *
     * <p>
     * 阿拉伯数字、罗马数字等
     */
    public static final Pattern NUMBER_PATTERN = Pattern.compile("\\p{N}");

    /** 其他字符-正则模式 */
    public static final Pattern OTHER_CHARS_PATTERN = Pattern.compile("\\p{C}");

    /**
     * 邮箱正则表达式
     *
     * <p>
     * https://blog.csdn.net/Architect_CSDN/article/details/89478042
     * https://www.cnblogs.com/lst619247/p/9289719.html
     *
     * <p>
     * 只有英文的邮箱。
     */
    public static final Pattern EMAIL_ENGLISH_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    /**
     * 允许中文前缀的邮箱正则表达式
     *
     * <p>
     * https://www.cnblogs.com/lst619247/p/9289719.html
     */
    public static final Pattern EMAIL_CHINESE_PATTERN =
            Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    /** 电话号码正则表达式 */
    public static final Pattern PHONE_PATTERN =
            Pattern.compile("^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\\\d{8}$");

    /**
     * URL 正则表达式
     *
     * <p>
     * （1）验证http,https,ftp开头 （2）验证一个":"，验证多个"/" （3）验证网址为 xxx.xxx （4）验证有0个或1个问号
     * （5）验证参数必须为xxx=xxx格式，且xxx=空格式通过 （6）验证参数与符号&连续个数为0个或1个
     *
     * <p>
     * <a href="https://www.cnblogs.com/woaiadu/p/7084250.html">...</a>
     */
    public static final Pattern URL_PATTERN =
            Pattern.compile(
                    "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+(\\\\?{0,1}(([A-Za-z0-9-~]+\\\\={0,1})([A-Za-z0-9-~]*)\\\\&{0,1})*)$");

    /** 网址正则 */
    public static final Pattern WEB_SITE_PATTERN =
            Pattern.compile("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$");

    /**
     * emoji 表情正则表达式 <a href="https://github.com/zly394/EmojiRegex">...</a>
     * <a href="https://github.com/vdurmont/emoji-java">...</a>
     */
    public static final Pattern EMOJI_PATTERN =
            Pattern.compile(
                    "(?:[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F]|[\\uD83D\\uDE80-\\uD83D\\uDEFF]|[\\u2600-\\u26FF]\\uFE0F?|[\\u2700-\\u27BF]\\uFE0F?|\\u24C2\\uFE0F?|[\\uD83C\\uDDE6-\\uD83C\\uDDFF]{1,2}|[\\uD83C\\uDD70\\uD83C\\uDD71\\uD83C\\uDD7E\\uD83C\\uDD7F\\uD83C\\uDD8E\\uD83C\\uDD91-\\uD83C\\uDD9A]\\uFE0F?|[\\u0023\\u002A\\u0030-\\u0039]\\uFE0F?\\u20E3|[\\u2194-\\u2199\\u21A9-\\u21AA]\\uFE0F?|[\\u2B05-\\u2B07\\u2B1B\\u2B1C\\u2B50\\u2B55]\\uFE0F?|[\\u2934\\u2935]\\uFE0F?|[\\u3030\\u303D]\\uFE0F?|[\\u3297\\u3299]\\uFE0F?|[\\uD83C\\uDE01\\uD83C\\uDE02\\uD83C\\uDE1A\\uD83C\\uDE2F\\uD83C\\uDE32-\\uD83C\\uDE3A\\uD83C\\uDE50\\uD83C\\uDE51]\\uFE0F?|[\\u203C\\u2049]\\uFE0F?|[\\u25AA\\u25AB\\u25B6\\u25C0\\u25FB-\\u25FE]\\uFE0F?|[\\u00A9\\u00AE]\\uFE0F?|[\\u2122\\u2139]\\uFE0F?|\\uD83C\\uDC04\\uFE0F?|\\uD83C\\uDCCF\\uFE0F?|[\\u231A\\u231B\\u2328\\u23CF\\u23E9-\\u23F3\\u23F8-\\u23FA]\\uFE0F?)");

    /** IP 对应的正则 */
    public static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}(.\\d{1,3}){3}$");

    /** 手机号 */
    public static final Pattern MOBILE = Pattern.compile("^1[3|4|5|8][0-9]\\d{8}$");

    /** 邮箱 */
    public static final Pattern EMAIL =
            Pattern.compile(
                    "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");

    public static boolean mobile(String v) {

        Matcher m = MOBILE.matcher(v);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    public static boolean email(String v) {

        Matcher m = EMAIL.matcher(v);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 是否为 ip
     * @param ip ip 地址
     * @return 结果
     */
    public static boolean isIp(final String ip) {
        if (StringUtils.isEmptyTrim(ip)) {
            return false;
        }

        return IP_PATTERN.matcher(ip).matches();
    }

    /**
     * 对特殊字符转移
     * @param keyword 特殊字符
     * @return 结果
     */
    public static String escapeWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            for (String key : SPECIAL_CHARS) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 是否为表情符号
     * @param string 字符串
     * @return 是否
     */
    public static boolean isEmoji(final String string) {
        return EMOJI_PATTERN.matcher(string).find();
    }

    /**
     * 是否为标点符号 中文符号：参考：https://blog.csdn.net/ztf312/article/details/54310542
     * @param string 字符
     * @return 结果
     */
    public static boolean isPunctuation(String string) {
        return isPatternMatch(string, PUNCTUATION_PATTERN);
    }

    /**
     * 是否为可标记的符号
     * @param string 字符
     * @return 结果
     */
    public static boolean isMarkable(String string) {
        return isPatternMatch(string, MARKABLE_PATTERN);
    }

    /**
     * 是否为字符
     * @param string 字符
     * @return 结果
     */
    public static boolean isSymbol(String string) {
        return isPatternMatch(string, SYMBOL_PATTERN);
    }

    /**
     * 是否为可标记的符号
     * @param string 字符
     * @return 结果
     */
    public static boolean isOtherChars(String string) {
        return isPatternMatch(string, OTHER_CHARS_PATTERN);
    }

    /// **
    // * 是否为数字
    // *
    // * @param string 字符
    // * @return 结果
    // */
    // public static boolean isNumber(String string) {
    // return isPatternMatch(string, NUMBER_PATTERN);
    // }

    /**
     * 是否为邮件
     * @param string 字符
     * @return 结果
     */
    public static boolean isEmail(final String string) {
        return isPatternMatch(string, EMAIL_ENGLISH_PATTERN);
    }

    /**
     * 是否为URL
     * @param string 字符
     * @return 结果
     */
    public static boolean isUrl(final String string) {
        return isPatternMatch(string, URL_PATTERN);
    }

    /**
     * 是否为网址
     * @param string 结果
     * @return 是否
     */
    public static boolean isWebSite(final String string) {
        return isPatternMatch(string, WEB_SITE_PATTERN);
    }

    /**
     * 验证字符串是否匹配正则表达式
     * @param string 字符串
     * @param pattern 正则表达式
     * @return 是否匹配
     */
    private static boolean isPatternMatch(final String string, final Pattern pattern) {
        return pattern.matcher(string).find();
    }

    /**
     * 验证数字
     * @param character 字符
     */
    public static boolean isNumber(String character) {
        return isPattern(character, REGEX_NUMBER);
    }

    /**
     * 验证是否为浮点数
     * @param character 字符
     * @return 返回结果
     */
    public static boolean isFloatPoint(String character) {
        return isPattern(character, REGEX_FLOAT_POINT);
    }

    /**
     * 只能输入n位的数字
     * @param character 字符
     */
    public static boolean isMaxLengthNumber(String character) {
        return isPattern(character, REGEX_MAX_LENGTH_NUMBER);
    }

    /**
     * 只能输入至少n位的数字
     * @param character 字符
     */
    public static boolean isLeastLengthNumber(String character) {
        return isPattern(character, REGEX_LEAST_LENGTH_NUMBER);
    }

    /**
     * 只能输入m~n位的数字
     * @param character 字符
     */
    public static boolean isSpecifiedLengthNumber(String character) {
        return isPattern(character, REGEX_SPECIFIED_LENGTH_NUMBER);
    }

    /**
     * 只能输入零和非零开头的数字
     * @param character 字符
     */
    public static boolean isNonZreoNumber(String character) {
        return isPattern(character, REGEX_NON_ZREO_NUMBER);
    }

    /**
     * 只能输入有两位小数的正实数
     * @param character 字符
     */
    public static boolean isTwoDecimalFractionNumber(String character) {
        return isPattern(character, REGEX_TWO_DECIMAL_FRACTION_NUMBER);
    }

    /**
     * 只能输入有1~3位小数的正实数
     * @param character 字符
     */
    public static boolean isSpecifiedDecimalFractionNumber(String character) {
        return isPattern(character, REGEX_SPECIFIED_DECIMAL_FRACTION_NUMBER);
    }

    /**
     * 只能输入非零的正整数
     * @param character 字符
     */
    public static boolean isNonzeroPositiveIntegersNumber(String character) {
        return isPattern(character, REGEX_NONZERO_POSITIVE_INTEGERS_NUMBER);
    }

    /**
     * 只能输入非零的负整数
     * @param character 字符
     */
    public static boolean isNonzeroNegativeIntegersNumber(String character) {
        return isPattern(character, REGEX_NONZERO_NEGATIVE_INTEGERS_NUMBER);
    }

    /**
     * 只能输入长度为3的字符
     * @param character 字符
     */
    public static boolean isThreeLengthCharacter(String character) {
        return isPattern(character, REGEX_THREE_LENGTH_CHARACTER);
    }

    /**
     * 只能输入由26个英文字母组成的字符串
     * @param character 字符
     */
    public static boolean isEnglishCharacter(String character) {
        return isPattern(character, REGEX_EMAIL_CHARACTER);
    }

    /**
     * 只能输入由26个大写英文字母组成的字符串
     * @param character 字符
     */
    public static boolean isCapitalizationEnglishCharacter(String character) {
        return isPattern(character, REGEX_CAPITALIZATION_ENGLISH_CHARACTER);
    }

    /**
     * 只能输入由26个小写英文字母组成的字符串
     * @param character 字符
     */
    public static boolean isLowercaseEnglishCharacter(String character) {
        return isPattern(character, REGEX_LOWERCASE_ENGLISH_CHARACTER);
    }

    /**
     * 只能输入由数字和26个英文字母组成的字符串
     * @param character 字符
     */
    public static boolean isEnglishAndNumberCharacter(String character) {
        return isPattern(character, REGEX_ENGLISH_AND_NUMBER_CHARACTER);
    }

    /**
     * 只能输入由数字、26个英文字母或者下划线组成的字符串
     * @param character 字符
     */
    public static boolean isNonSpecialCharacter(String character) {
        return isPattern(character, REGEX_NON_SPECIAL_CHARACTER);
    }

    /**
     * 验证用户密码 正确格式为：以字母开头，长度在6~18之间，只能包含字符、数字和下划线;
     * @param character 字符
     */
    public static boolean isPasswordCharacter(String character) {
        return isPattern(character, REGEX_PASSWORD_CHARACTER);
    }

    /**
     * 只能输入汉字
     * @param character 字符
     */
    public static boolean isChineseCharacter(String character) {
        return isPattern(character, REGEX_CHINESE_CHARACTER);
    }

    /**
     * 验证Email地址
     * @param character 字符
     */
    public static boolean isEmailCharacter(String character) {
        return isPattern(character, REGEX_EMAIL_CHARACTER);
    }

    /**
     * 验证InternetURL
     * @param character 字符
     */
    public static boolean isInternetUrlCharacter(String character) {
        return isPattern(character, REGEX_INTERNET_URL_CHARACTER);
    }

    /**
     * 验证电话号码
     * 正确格式为："XXX-XXXXXXX"、"XXXX-XXXXXXXX"、"XXX-XXXXXXX"、"XXX-XXXXXXXX"、"XXXXXXX"和"XXXXXXXX";
     * @param character 字符
     */
    public static boolean isPhoneCharacter(String character) {
        return isPattern(character, REGEX_PHONE_CHARACTER);
    }

    /**
     * 验证身份证号（15位或18位数字）
     * @param character 字符
     */
    public static boolean isIdNumberCharacter(String character) {
        return isPattern(character, REGEX_ID_NUMBER_CHARACTER);
    }

    /**
     * 验证是否为UUID
     * @param character 值
     * @return
     */
    public static boolean isUuidCharacter(String character) {
        return isPattern(character, REGEX_UUID);
    }

    /**
     * 验证是否为指定类型
     * @param character 字符
     * @param regex 正则
     * @return 返回布值
     */
    public static boolean isPattern(String character, String regex) {
        // 正则
        Pattern pattern = compile(regex);
        // 验证
        Matcher matcher = pattern.matcher(character);
        // 返回结果
        return matcher.matches();
    }
}
