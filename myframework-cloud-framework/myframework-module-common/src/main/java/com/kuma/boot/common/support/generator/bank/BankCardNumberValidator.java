/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.kuma.boot.common.support.generator.bank;

import com.kuma.boot.common.support.generator.util.LuhnUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class BankCardNumberValidator {
    public static boolean validate(String cardNo) {
        if (StringUtils.isEmpty(cardNo)) {
            return false;
        }
        if (!NumberUtils.isDigits((String)cardNo)) {
            return false;
        }
        if (cardNo.length() > 19 || cardNo.length() < 16) {
            return false;
        }
        int luhnSum = LuhnUtils.getLuhnSum(cardNo.substring(0, cardNo.length() - 1).trim().toCharArray());
        char checkCode = luhnSum % 10 == 0 ? (char)'0' : (char)(10 - luhnSum % 10 + 48);
        return cardNo.substring(cardNo.length() - 1).charAt(0) == checkCode;
    }
}

