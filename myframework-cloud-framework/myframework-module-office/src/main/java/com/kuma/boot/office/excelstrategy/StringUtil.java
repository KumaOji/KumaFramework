/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.excelstrategy;

public class StringUtil {
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String replace(String strSource, String strFrom, String strTo) {
        int intPos;
        if (strFrom == null || "".equals(strFrom)) {
            return strSource;
        }
        Object strDest = "";
        int intFromLen = strFrom.length();
        while ((intPos = strSource.indexOf(strFrom)) != -1) {
            strDest = (String)strDest + strSource.substring(0, intPos);
            strDest = (String)strDest + strTo;
            strSource = strSource.substring(intPos + intFromLen);
        }
        strDest = (String)strDest + strSource;
        return strDest;
    }
}

