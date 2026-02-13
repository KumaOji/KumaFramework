/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lang;

import com.kuma.boot.common.utils.lang.StringUtils;

public final class CharUtils {
    private CharUtils() {
    }

    public static boolean isEmpty(Character character) {
        return character == null;
    }

    public static boolean isNotEmpty(Character character) {
        return !CharUtils.isEmpty(character);
    }

    public static String repeat(char unit, int times) {
        String component = String.valueOf(unit);
        return StringUtils.repeat(component, times);
    }

    public static char toHalfWidth(char c) {
        char resultChar = c;
        if (resultChar == '\u3000') {
            resultChar = ' ';
        } else if (resultChar > '\uff00' && resultChar < '\uff5f') {
            resultChar = (char)(resultChar - 65248);
        }
        return resultChar;
    }

    public static char toFullWidth(char c) {
        char resultChar = c;
        if (resultChar == ' ') {
            resultChar = '\u3000';
        } else if (resultChar >= '!' && resultChar <= '~') {
            resultChar = (char)(resultChar + 65248);
        }
        return resultChar;
    }

    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        return sc == Character.UnicodeScript.HAN;
    }

    public static boolean isSpace(char c) {
        return Character.isSpaceChar(c) || '\u0013' == c;
    }

    public static boolean isNotSpace(char c) {
        return !CharUtils.isSpace(c);
    }

    public static boolean isDigitOrLetter(char c) {
        return Character.isDigit(c) || Character.isLowerCase(c) || Character.isUpperCase(c);
    }

    public static boolean isEmilChar(char c) {
        return CharUtils.isDigitOrLetter(c) || '_' == c || '-' == c || c == '.' || c == '@';
    }

    public static boolean isWebSiteChar(char c) {
        return CharUtils.isDigitOrLetter(c) || '-' == c || '.' == c;
    }

    public static boolean isChinese(char ch) {
        return ch >= '\u4e00' && ch <= '\u9fa5';
    }

    public static boolean isEnglish(char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z';
    }

    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isAscii(char c) {
        return c <= '\u007f';
    }

    public static boolean isNotAscii(char c) {
        return !CharUtils.isAscii(c);
    }
}

