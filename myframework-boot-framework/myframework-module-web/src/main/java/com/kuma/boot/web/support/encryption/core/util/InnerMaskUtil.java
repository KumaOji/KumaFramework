/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.web.support.encryption.core.util;

import com.kuma.boot.common.utils.lang.StringUtils;

public final class InnerMaskUtil {
    private InnerMaskUtil() {
    }

    public static String phone(String phone) {
        if (StringUtils.isEmpty((String)phone)) {
            return phone;
        }
        if (phone.length() <= 3) {
            return phone;
        }
        int prefixLength = 3;
        String middle = "****";
        return StringUtils.buildString((Object)phone, (String)"****", (int)3);
    }

    public static String email(String email) {
        if (StringUtils.isEmpty((String)email)) {
            return email;
        }
        int prefixLength = 3;
        int atIndex = email.indexOf("@");
        String middle = "****";
        if (atIndex > 0) {
            int middleLength = atIndex - 3;
            middle = StringUtils.repeat((String)"*", (int)middleLength);
        }
        return StringUtils.buildString((Object)email, (String)middle, (int)3);
    }

    public static String chineseName(String chineseName) {
        if (StringUtils.isEmpty((String)chineseName)) {
            return chineseName;
        }
        int nameLength = chineseName.length();
        if (1 == nameLength) {
            return chineseName;
        }
        if (2 == nameLength) {
            return "*" + chineseName.charAt(1);
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(chineseName.charAt(0));
        for (int i = 0; i < nameLength - 2; ++i) {
            stringBuffer.append("*");
        }
        stringBuffer.append(chineseName.charAt(nameLength - 1));
        return stringBuffer.toString();
    }

    public static String bankCardNum(String cardId) {
        if (StringUtils.isEmpty((String)cardId)) {
            return cardId;
        }
        int prefixLength = 6;
        int times = cardId.length() - 10;
        String middle = StringUtils.repeat((String)"*", (int)times);
        return StringUtils.buildString((Object)cardId, (String)middle, (int)6);
    }

    public static String idCard(String cardId) {
        if (StringUtils.isEmpty((String)cardId)) {
            return cardId;
        }
        int prefixLength = 6;
        int times = cardId.length() - 9;
        String middle = StringUtils.repeat((String)"*", (int)times);
        return StringUtils.buildString((Object)cardId, (String)middle, (int)6);
    }

    public static String address(String address) {
        if (StringUtils.isEmpty((String)address)) {
            return address;
        }
        int prefixLength = 6;
        int times = address.length() - 10;
        String middle = StringUtils.repeat((String)"*", (int)times);
        return StringUtils.buildString((Object)address, (String)middle, (int)6);
    }

    public static String password(String password) {
        if (StringUtils.isEmpty((String)password)) {
            return password;
        }
        return StringUtils.repeat((String)"*", (int)password.length());
    }
}

