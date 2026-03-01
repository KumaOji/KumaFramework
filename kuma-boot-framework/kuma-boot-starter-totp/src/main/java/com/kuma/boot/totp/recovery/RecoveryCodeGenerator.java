/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.recovery;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class RecoveryCodeGenerator {
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int CODE_LENGTH = 16;
    private static final int GROUPS_NBR = 4;
    private Random random = new SecureRandom();

    public String[] generateCodes(int amount) {
        if (amount < 1) {
            throw new InvalidParameterException("Amount must be at least 1.");
        }
        String[] codes = new String[amount];
        Arrays.setAll(codes, i -> this.generateCode());
        return codes;
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(19);
        for (int i = 0; i < 16; ++i) {
            code.append(CHARACTERS[this.random.nextInt(CHARACTERS.length)]);
            if ((i + 1) % 4 != 0 || i + 1 == 16) continue;
            code.append("-");
        }
        return code.toString();
    }
}

