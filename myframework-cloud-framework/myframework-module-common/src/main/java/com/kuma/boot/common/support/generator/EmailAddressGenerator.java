/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.RandomStringUtils
 */
package com.kuma.boot.common.support.generator;

import com.kuma.boot.common.support.generator.base.GenericGenerator;
import org.apache.commons.lang3.RandomStringUtils;

public class EmailAddressGenerator
extends GenericGenerator {
    private static final GenericGenerator INSTANCE = new EmailAddressGenerator();

    private EmailAddressGenerator() {
    }

    public static GenericGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        String result = RandomStringUtils.randomAlphanumeric((int)10) + "@" + RandomStringUtils.randomAlphanumeric((int)5) + "." + RandomStringUtils.randomAlphanumeric((int)3);
        return result.toLowerCase();
    }
}

