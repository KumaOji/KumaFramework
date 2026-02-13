/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.generator;

import com.kuma.boot.common.support.generator.base.GenericGenerator;
import com.kuma.boot.common.support.generator.util.ChineseCharUtils;

public class ChineseNameGenerator
extends GenericGenerator {
    private static final String[] FIRST_NAMES = new String[]{"\u674e", "\u738b", "\u5f20", "\u5218", "\u9648", "\u6768", "\u9ec4", "\u8d75", "\u5468", "\u5434", "\u5f90", "\u5b59", "\u6731", "\u9a6c", "\u80e1", "\u90ed", "\u6797", "\u4f55", "\u9ad8", "\u6881", "\u90d1", "\u7f57", "\u5b8b", "\u8c22", "\u5510", "\u97e9", "\u66f9", "\u8bb8", "\u9093", "\u8427", "\u51af", "\u66fe", "\u7a0b", "\u8521", "\u5f6d", "\u6f58", "\u8881", "\u65bc", "\u8463", "\u4f59", "\u82cf", "\u53f6", "\u5415", "\u9b4f", "\u848b", "\u7530", "\u675c", "\u4e01", "\u6c88", "\u59dc", "\u8303", "\u6c5f", "\u5085", "\u949f", "\u5362", "\u6c6a", "\u6234", "\u5d14", "\u4efb", "\u9646", "\u5ed6", "\u59da", "\u65b9", "\u91d1", "\u90b1", "\u590f", "\u8c2d", "\u97e6", "\u8d3e", "\u90b9", "\u77f3", "\u718a", "\u5b5f", "\u79e6", "\u960e", "\u859b", "\u4faf", "\u96f7", "\u767d", "\u9f99", "\u6bb5", "\u90dd", "\u5b54", "\u90b5", "\u53f2", "\u6bdb", "\u5e38", "\u4e07", "\u987e", "\u8d56", "\u6b66", "\u5eb7", "\u8d3a", "\u4e25", "\u5c39", "\u94b1", "\u65bd", "\u725b", "\u6d2a", "\u9f9a", "\u4e1c\u65b9", "\u590f\u4faf", "\u8bf8\u845b", "\u5c09\u8fdf", "\u7687\u752b", "\u5b87\u6587", "\u9c9c\u4e8e", "\u897f\u95e8", "\u53f8\u9a6c", "\u72ec\u5b64", "\u516c\u5b59", "\u6155\u5bb9", "\u8f69\u8f95", "\u5de6\u4e18", "\u6b27\u9633", "\u7687\u752b", "\u4e0a\u5b98", "\u95fe\u4e18", "\u4ee4\u72d0"};
    private static final ChineseNameGenerator INSTANCE = new ChineseNameGenerator();

    private ChineseNameGenerator() {
    }

    public static ChineseNameGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        return this.genFirstName() + ChineseCharUtils.genRandomLengthChineseChars(1, 2);
    }

    private String genFirstName() {
        return FIRST_NAMES[this.getRandomInstance().nextInt(FIRST_NAMES.length)];
    }

    public String generateOdd() {
        return this.genFirstName() + ChineseCharUtils.getOneOddChar();
    }
}

