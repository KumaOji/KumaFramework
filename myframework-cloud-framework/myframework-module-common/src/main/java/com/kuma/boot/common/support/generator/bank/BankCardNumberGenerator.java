/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.generator.bank;

import com.kuma.boot.common.support.generator.bank.BankCardTypeEnum;
import com.kuma.boot.common.support.generator.bank.BankNameEnum;
import com.kuma.boot.common.support.generator.base.GenericGenerator;
import com.kuma.boot.common.support.generator.util.LuhnUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Random;

public class BankCardNumberGenerator
extends GenericGenerator {
    private static GenericGenerator instance = new BankCardNumberGenerator();

    private BankCardNumberGenerator() {
    }

    public static GenericGenerator getInstance() {
        return instance;
    }

    @Override
    public String generate() {
        Random random = this.getRandomInstance();
        Integer prev = 622126 + random.nextInt(800);
        return BankCardNumberGenerator.generateByPrefix(prev);
    }

    public static String generateByPrefix(Integer prefix) {
        Random random = new Random(System.currentTimeMillis());
        String bardNo = prefix + StringUtils.leftPad("" + random.nextInt(999999999), 9, "0");
        char[] chs = bardNo.trim().toCharArray();
        int luhnSum = LuhnUtils.getLuhnSum(chs);
        char checkCode = luhnSum % 10 == 0 ? (char)'0' : (char)(10 - luhnSum % 10 + 48);
        return bardNo + checkCode;
    }

    public static String generate(BankNameEnum bankName, BankCardTypeEnum cardType) {
        Integer[] candidatePrefixes = null;
        if (cardType == null) {
            candidatePrefixes = bankName.getAllCardPrefixes();
        } else {
            switch (cardType) {
                case DEBIT: {
                    candidatePrefixes = bankName.getDebitCardPrefixes();
                    break;
                }
                case CREDIT: {
                    candidatePrefixes = bankName.getCreditCardPrefixes();
                    break;
                }
            }
        }
        if (candidatePrefixes == null || candidatePrefixes.length == 0) {
            throw new RuntimeException("\u6ca1\u6709\u8be5\u94f6\u884c\u7684\u76f8\u5173\u5361\u53f7\u4fe1\u606f");
        }
        Integer prefix = candidatePrefixes[new Random().nextInt(candidatePrefixes.length)];
        return BankCardNumberGenerator.generateByPrefix(prefix);
    }
}

