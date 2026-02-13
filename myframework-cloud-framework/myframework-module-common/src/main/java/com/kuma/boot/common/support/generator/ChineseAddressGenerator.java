/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.RandomUtil
 */
package com.kuma.boot.common.support.generator;

import cn.hutool.core.util.RandomUtil;
import com.kuma.boot.common.support.generator.ChineseAreaList;
import com.kuma.boot.common.support.generator.base.GenericGenerator;
import com.kuma.boot.common.support.generator.util.ChineseCharUtils;

public class ChineseAddressGenerator
extends GenericGenerator {
    private static final GenericGenerator INSTANCE = new ChineseAddressGenerator();

    private ChineseAddressGenerator() {
    }

    public static GenericGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        return ChineseAddressGenerator.genProvinceAndCity() + ChineseCharUtils.genRandomLengthChineseChars(2, 3) + "\u8def" + RandomUtil.randomInt((int)1, (int)8000) + "\u53f7" + ChineseCharUtils.genRandomLengthChineseChars(2, 3) + "\u5c0f\u533a" + RandomUtil.randomInt((int)1, (int)20) + "\u5355\u5143" + RandomUtil.randomInt((int)101, (int)2500) + "\u5ba4";
    }

    private static String genProvinceAndCity() {
        return ChineseAreaList.provinceCityList.get(RandomUtil.randomInt((int)0, (int)ChineseAreaList.provinceCityList.size()));
    }
}

