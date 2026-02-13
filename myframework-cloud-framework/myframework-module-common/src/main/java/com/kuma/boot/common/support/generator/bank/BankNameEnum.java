/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.kuma.boot.common.support.generator.bank;

import org.apache.commons.lang3.ArrayUtils;

public enum BankNameEnum {
    ICBC("102", "\u4e2d\u56fd\u5de5\u5546\u94f6\u884c", "\u5de5\u884c", new Integer[]{622200, 955880}, new Integer[]{427020, 427030, 530990, 622230, 622235, 622210, 622215}),
    ABC("103", "\u4e2d\u56fd\u519c\u4e1a\u94f6\u884c", "\u519c\u884c"),
    BOC("104", "\u4e2d\u56fd\u94f6\u884c", "\u4e2d\u884c"),
    CCB("105", "\u4e2d\u56fd\u5efa\u8bbe\u94f6\u884c", "\u5efa\u884c"),
    BCOM("301", "\u4ea4\u901a\u94f6\u884c", "\u4ea4\u884c"),
    CITIC("302", "\u4e2d\u4fe1\u94f6\u884c"),
    CEB("303", "\u4e2d\u56fd\u5149\u5927\u94f6\u884c"),
    HXB("304", "\u534e\u590f\u94f6\u884c"),
    CMBC("305", "\u4e2d\u56fd\u6c11\u751f\u94f6\u884c"),
    CGB("306", "\u5e7f\u4e1c\u53d1\u5c55\u94f6\u884c"),
    PAB("307", "\u5e73\u5b89\u94f6\u884c"),
    CMB("308", "\u62db\u5546\u94f6\u884c", "\u62db\u884c"),
    CIB("309", "\u5174\u4e1a\u94f6\u884c"),
    SPDB("310", "\u4e0a\u6d77\u6d66\u4e1c\u53d1\u5c55\u94f6\u884c"),
    CR("999999", "\u534e\u6da6\u94f6\u884c", new Integer[]{622363}),
    BHB("318", "\u6e24\u6d77\u94f6\u884c"),
    HSB("319", "\u5fbd\u5546\u94f6\u884c"),
    JSB_1("03133010", "\u6c5f\u82cf\u94f6\u884c"),
    JSB("03133120", "\u6c5f\u82cf\u94f6\u884c"),
    SHB("04012900", "\u4e0a\u6d77\u94f6\u884c"),
    POST("403", "\u4e2d\u56fd\u90ae\u653f\u50a8\u84c4\u94f6\u884c"),
    BOB("", "\u5317\u4eac\u94f6\u884c"),
    BON("", "\u5b81\u6ce2\u94f6\u884c");

    private final String code;
    private final String name;
    private String abbrName;
    private Integer[] creditCardPrefixes;
    private Integer[] debitCardPrefixes;
    private Integer[] allCardPrefixes;

    private BankNameEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private BankNameEnum(String code, String name, String abbrName) {
        this.code = code;
        this.name = name;
        this.abbrName = abbrName;
    }

    private BankNameEnum(String code, String name, String abbrName, Integer[] debitCardPrefixes, Integer[] creditCardPrefixes) {
        this.code = code;
        this.name = name;
        this.abbrName = abbrName;
        this.creditCardPrefixes = creditCardPrefixes;
        this.debitCardPrefixes = debitCardPrefixes;
        this.allCardPrefixes = (Integer[])ArrayUtils.addAll((Object[])this.creditCardPrefixes, (Object[])this.debitCardPrefixes);
    }

    private BankNameEnum(String code, String name, Integer[] debitCardPrefixes) {
        this.code = code;
        this.name = name;
        this.debitCardPrefixes = debitCardPrefixes;
        this.allCardPrefixes = debitCardPrefixes;
    }

    private BankNameEnum(String code, String name, Integer[] debitCardPrefixes, Integer[] creditCardPrefixes) {
        this.code = code;
        this.name = name;
        this.creditCardPrefixes = creditCardPrefixes;
        this.debitCardPrefixes = debitCardPrefixes;
        this.allCardPrefixes = (Integer[])ArrayUtils.addAll((Object[])this.creditCardPrefixes, (Object[])this.debitCardPrefixes);
    }

    public String getName() {
        return this.name;
    }

    public String getAbbrName() {
        return this.abbrName;
    }

    public Integer[] getCreditCardPrefixes() {
        return this.creditCardPrefixes;
    }

    public Integer[] getDebitCardPrefixes() {
        return this.debitCardPrefixes;
    }

    public Integer[] getAllCardPrefixes() {
        return this.allCardPrefixes;
    }

    public String getCode() {
        return this.code;
    }
}

