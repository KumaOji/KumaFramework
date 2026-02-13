/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;

public class CodeUtils {
    private static final int NUM_LENGTH = 2;
    public static final int ZHANWEI_LENGTH = 3;
    public static final char LETTER = 'Z';

    public static synchronized String getNextYouBianCode(String code) {
        String newcode = "";
        if (StringUtils.isEmpty(code)) {
            String zimu = "A";
            String num = CodeUtils.getStrNum(1);
            newcode = zimu + num;
        } else {
            String beforeCode = code.substring(0, code.length() - 1 - 2);
            String afterCode = code.substring(code.length() - 1 - 2, code.length());
            char afterCodeZimu = afterCode.substring(0, 1).charAt(0);
            Integer afterCodeNum = Integer.parseInt(afterCode.substring(1));
            String nextNum = "";
            char nextZimu = 'A';
            nextNum = afterCodeNum == CodeUtils.getMaxNumByLength(2) ? CodeUtils.getNextStrNum(0) : CodeUtils.getNextStrNum(afterCodeNum);
            nextZimu = afterCodeNum == CodeUtils.getMaxNumByLength(2) ? (char)CodeUtils.getNextZiMu(afterCodeZimu) : (char)afterCodeZimu;
            newcode = 'Z' == afterCodeZimu && CodeUtils.getMaxNumByLength(2) == afterCodeNum ? code + nextZimu + nextNum : beforeCode + nextZimu + nextNum;
        }
        return newcode;
    }

    public static synchronized String getSubYouBianCode(String parentCode, String localCode) {
        if (localCode != null && localCode != "") {
            return CodeUtils.getNextYouBianCode(localCode);
        }
        parentCode = (String)parentCode + "A" + CodeUtils.getNextStrNum(0);
        return parentCode;
    }

    private static String getNextStrNum(int num) {
        return CodeUtils.getStrNum(CodeUtils.getNextNum(num));
    }

    private static String getStrNum(int num) {
        String s = String.format("%02d", num);
        return s;
    }

    private static int getNextNum(int num) {
        return ++num;
    }

    private static char getNextZiMu(char zimu) {
        if (zimu == 'Z') {
            return 'A';
        }
        zimu = (char)(zimu + '\u0001');
        return zimu;
    }

    private static int getMaxNumByLength(int length) {
        if (length == 0) {
            return 0;
        }
        StringBuilder maxNum = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            maxNum.append("9");
        }
        return Integer.parseInt(maxNum.toString());
    }

    public static String[] cutYouBianCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        int c = code.length() / 3;
        String[] cutcode = new String[c];
        for (int i = 0; i < c; ++i) {
            cutcode[i] = code.substring(0, (i + 1) * 3);
        }
        return cutcode;
    }
}

