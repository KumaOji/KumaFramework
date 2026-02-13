/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {
    public static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    public static final String REGEX_NUMBER = "^[+-]?[0-9]*$";
    public static final String REGEX_FLOAT_POINT = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
    public static final String REGEX_MAX_LENGTH_NUMBER = "^\\d{n}$";
    public static final String REGEX_LEAST_LENGTH_NUMBER = "^\\d{n,}$";
    public static final String REGEX_SPECIFIED_LENGTH_NUMBER = "^\\d{m,n}$";
    public static final String REGEX_NON_ZREO_NUMBER = "^(0|[1-9][0-9]*)$";
    public static final String REGEX_TWO_DECIMAL_FRACTION_NUMBER = "^[0-9]+(.[0-9]{2})?$";
    public static final String REGEX_SPECIFIED_DECIMAL_FRACTION_NUMBER = "^[0-9]+(.[0-9]{2})?$";
    public static final String REGEX_NONZERO_POSITIVE_INTEGERS_NUMBER = "^\\+?[1-9][0-9]*$";
    public static final String REGEX_NONZERO_NEGATIVE_INTEGERS_NUMBER = "^\\-[1-9][]0-9*$";
    public static final String REGEX_THREE_LENGTH_CHARACTER = "^.{3}$";
    public static final String REGEX_ENGLISH_CHARACTER = "^[A-Za-z]+$";
    public static final String REGEX_CAPITALIZATION_ENGLISH_CHARACTER = "^[A-Z]+$";
    public static final String REGEX_LOWERCASE_ENGLISH_CHARACTER = "^[a-z]+$";
    public static final String REGEX_ENGLISH_AND_NUMBER_CHARACTER = "^[A-Za-z0-9]+$";
    public static final String REGEX_NON_SPECIAL_CHARACTER = "^\\w+$";
    public static final String REGEX_PASSWORD_CHARACTER = "^[a-zA-Z]\\w{5,17}$";
    public static final String REGEX_CHINESE_CHARACTER = "^[\u4e00-\u9fa5]{0,}$";
    public static final String REGEX_EMAIL_CHARACTER = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    public static final String REGEX_INTERNET_URL_CHARACTER = "^http://%28[/\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
    public static final String REGEX_PHONE_CHARACTER = "^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$";
    public static final String REGEX_ID_NUMBER_CHARACTER = "^\\d{15}|\\d{18}$";
    public static final String IE = "msie ([\\d.]+)";
    public static final String IE_EDGE = "edge ([\\d.]+)";
    public static final String MICROSOFT_EDGE = "edg/([\\d.]+)";
    public static final String FIREFOX = "firefox/([\\d.]+)";
    public static final String CHROME = "chrome/([\\d.]+)";
    public static final String OPERA = "opr/([\\d.]+)";
    public static final String SAFARI = "version/([\\d.]+).*safari";
    public static final String[] SPECIAL_CHARS = new String[]{"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
    public static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{P}");
    public static final Pattern LETTER_PATTERN = Pattern.compile("\\p{L}");
    public static final Pattern MARKABLE_PATTERN = Pattern.compile("\\p{M}");
    public static final Pattern DELIMITER_PATTERN = Pattern.compile("\\p{Z}");
    public static final Pattern SYMBOL_PATTERN = Pattern.compile("\\p{S}");
    public static final Pattern NUMBER_PATTERN = Pattern.compile("\\p{N}");
    public static final Pattern OTHER_CHARS_PATTERN = Pattern.compile("\\p{C}");
    public static final Pattern EMAIL_ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    public static final Pattern EMAIL_CHINESE_PATTERN = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    public static final Pattern PHONE_PATTERN = Pattern.compile("^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\\\d{8}$");
    public static final Pattern URL_PATTERN = Pattern.compile("^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+(\\\\?{0,1}(([A-Za-z0-9-~]+\\\\={0,1})([A-Za-z0-9-~]*)\\\\&{0,1})*)$");
    public static final Pattern WEB_SITE_PATTERN = Pattern.compile("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$");
    public static final Pattern EMOJI_PATTERN = Pattern.compile("(?:[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F]|[\\uD83D\\uDE80-\\uD83D\\uDEFF]|[\\u2600-\\u26FF]\\uFE0F?|[\\u2700-\\u27BF]\\uFE0F?|\\u24C2\\uFE0F?|[\\uD83C\\uDDE6-\\uD83C\\uDDFF]{1,2}|[\\uD83C\\uDD70\\uD83C\\uDD71\\uD83C\\uDD7E\\uD83C\\uDD7F\\uD83C\\uDD8E\\uD83C\\uDD91-\\uD83C\\uDD9A]\\uFE0F?|[\\u0023\\u002A\\u0030-\\u0039]\\uFE0F?\\u20E3|[\\u2194-\\u2199\\u21A9-\\u21AA]\\uFE0F?|[\\u2B05-\\u2B07\\u2B1B\\u2B1C\\u2B50\\u2B55]\\uFE0F?|[\\u2934\\u2935]\\uFE0F?|[\\u3030\\u303D]\\uFE0F?|[\\u3297\\u3299]\\uFE0F?|[\\uD83C\\uDE01\\uD83C\\uDE02\\uD83C\\uDE1A\\uD83C\\uDE2F\\uD83C\\uDE32-\\uD83C\\uDE3A\\uD83C\\uDE50\\uD83C\\uDE51]\\uFE0F?|[\\u203C\\u2049]\\uFE0F?|[\\u25AA\\u25AB\\u25B6\\u25C0\\u25FB-\\u25FE]\\uFE0F?|[\\u00A9\\u00AE]\\uFE0F?|[\\u2122\\u2139]\\uFE0F?|\\uD83C\\uDC04\\uFE0F?|\\uD83C\\uDCCF\\uFE0F?|[\\u231A\\u231B\\u2328\\u23CF\\u23E9-\\u23F3\\u23F8-\\u23FA]\\uFE0F?)");
    public static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}(.\\d{1,3}){3}$");
    public static final Pattern MOBILE = Pattern.compile("^1[3|4|5|8][0-9]\\d{8}$");
    public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");

    private RegexUtils() {
    }

    public static boolean mobile(String v) {
        Matcher m = MOBILE.matcher(v);
        return m.matches();
    }

    public static boolean email(String v) {
        Matcher m = EMAIL.matcher(v);
        return m.matches();
    }

    public static boolean isIp(String ip) {
        if (StringUtils.isEmptyTrim(ip)) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches();
    }

    public static String escapeWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            for (String key : SPECIAL_CHARS) {
                if (!keyword.contains(key)) continue;
                keyword = keyword.replace(key, "\\" + key);
            }
        }
        return keyword;
    }

    public static boolean isEmoji(String string) {
        return EMOJI_PATTERN.matcher(string).find();
    }

    public static boolean isPunctuation(String string) {
        return RegexUtils.isPatternMatch(string, PUNCTUATION_PATTERN);
    }

    public static boolean isMarkable(String string) {
        return RegexUtils.isPatternMatch(string, MARKABLE_PATTERN);
    }

    public static boolean isSymbol(String string) {
        return RegexUtils.isPatternMatch(string, SYMBOL_PATTERN);
    }

    public static boolean isOtherChars(String string) {
        return RegexUtils.isPatternMatch(string, OTHER_CHARS_PATTERN);
    }

    public static boolean isEmail(String string) {
        return RegexUtils.isPatternMatch(string, EMAIL_ENGLISH_PATTERN);
    }

    public static boolean isUrl(String string) {
        return RegexUtils.isPatternMatch(string, URL_PATTERN);
    }

    public static boolean isWebSite(String string) {
        return RegexUtils.isPatternMatch(string, WEB_SITE_PATTERN);
    }

    private static boolean isPatternMatch(String string, Pattern pattern) {
        return pattern.matcher(string).find();
    }

    public static boolean isNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_NUMBER);
    }

    public static boolean isFloatPoint(String character) {
        return RegexUtils.isPattern(character, REGEX_FLOAT_POINT);
    }

    public static boolean isMaxLengthNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_MAX_LENGTH_NUMBER);
    }

    public static boolean isLeastLengthNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_LEAST_LENGTH_NUMBER);
    }

    public static boolean isSpecifiedLengthNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_SPECIFIED_LENGTH_NUMBER);
    }

    public static boolean isNonZreoNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_NON_ZREO_NUMBER);
    }

    public static boolean isTwoDecimalFractionNumber(String character) {
        return RegexUtils.isPattern(character, "^[0-9]+(.[0-9]{2})?$");
    }

    public static boolean isSpecifiedDecimalFractionNumber(String character) {
        return RegexUtils.isPattern(character, "^[0-9]+(.[0-9]{2})?$");
    }

    public static boolean isNonzeroPositiveIntegersNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_NONZERO_POSITIVE_INTEGERS_NUMBER);
    }

    public static boolean isNonzeroNegativeIntegersNumber(String character) {
        return RegexUtils.isPattern(character, REGEX_NONZERO_NEGATIVE_INTEGERS_NUMBER);
    }

    public static boolean isThreeLengthCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_THREE_LENGTH_CHARACTER);
    }

    public static boolean isEnglishCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_EMAIL_CHARACTER);
    }

    public static boolean isCapitalizationEnglishCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_CAPITALIZATION_ENGLISH_CHARACTER);
    }

    public static boolean isLowercaseEnglishCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_LOWERCASE_ENGLISH_CHARACTER);
    }

    public static boolean isEnglishAndNumberCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_ENGLISH_AND_NUMBER_CHARACTER);
    }

    public static boolean isNonSpecialCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_NON_SPECIAL_CHARACTER);
    }

    public static boolean isPasswordCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_PASSWORD_CHARACTER);
    }

    public static boolean isChineseCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_CHINESE_CHARACTER);
    }

    public static boolean isEmailCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_EMAIL_CHARACTER);
    }

    public static boolean isInternetUrlCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_INTERNET_URL_CHARACTER);
    }

    public static boolean isPhoneCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_PHONE_CHARACTER);
    }

    public static boolean isIdNumberCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_ID_NUMBER_CHARACTER);
    }

    public static boolean isUuidCharacter(String character) {
        return RegexUtils.isPattern(character, REGEX_UUID);
    }

    public static boolean isPattern(String character, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(character);
        return matcher.matches();
    }
}

