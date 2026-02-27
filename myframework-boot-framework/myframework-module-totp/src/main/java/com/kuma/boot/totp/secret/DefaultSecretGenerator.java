/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base32
 */
package com.kuma.boot.totp.secret;

import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base32;

public class DefaultSecretGenerator
implements SecretGenerator {
    private final SecureRandom randomBytes = new SecureRandom();
    private static final Base32 encoder = new Base32();
    private final int numCharacters;

    public DefaultSecretGenerator() {
        this.numCharacters = 32;
    }

    public DefaultSecretGenerator(int numCharacters) {
        this.numCharacters = numCharacters;
    }

    @Override
    public String generate() {
        return new String(encoder.encode(this.getRandomBytes()));
    }

    private byte[] getRandomBytes() {
        byte[] bytes = new byte[this.numCharacters * 5 / 8];
        this.randomBytes.nextBytes(bytes);
        return bytes;
    }
}

