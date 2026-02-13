/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.RandomUtil
 */
package com.kuma.boot.common.support.generator;

import cn.hutool.core.util.RandomUtil;
import com.kuma.boot.common.support.generator.base.GenericGenerator;
import com.kuma.boot.common.utils.lang.StringUtils;

public class ChineseMobileNumberGenerator
extends GenericGenerator {
    private static final int[] MOBILE_PREFIX = new int[]{133, 153, 177, 180, 181, 189, 134, 135, 136, 137, 138, 139, 150, 151, 152, 157, 158, 159, 178, 182, 183, 184, 187, 188, 130, 131, 132, 155, 156, 176, 185, 186, 145, 147, 170};
    private static final ChineseMobileNumberGenerator INSTANCE = new ChineseMobileNumberGenerator();

    private ChineseMobileNumberGenerator() {
    }

    public static ChineseMobileNumberGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        return ChineseMobileNumberGenerator.genMobilePre() + StringUtils.leftPad("" + RandomUtil.randomInt((int)0, (int)100000000), 8, "0");
    }

    public String generateFake() {
        return "19" + StringUtils.leftPad("" + RandomUtil.randomInt((int)0, (int)1000000000), 9, "0");
    }

    private static String genMobilePre() {
        return "" + MOBILE_PREFIX[RandomUtil.randomInt((int)0, (int)MOBILE_PREFIX.length)];
    }
}

