/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base32
 */
package com.kuma.boot.totp.code;

import com.kuma.boot.totp.exceptions.CodeGenerationException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;

public class DefaultCodeGenerator
implements CodeGenerator {
    private final HashingAlgorithm algorithm;
    private final int digits;

    public DefaultCodeGenerator() {
        this(HashingAlgorithm.SHA1, 6);
    }

    public DefaultCodeGenerator(HashingAlgorithm algorithm) {
        this(algorithm, 6);
    }

    public DefaultCodeGenerator(HashingAlgorithm algorithm, int digits) {
        if (algorithm == null) {
            throw new InvalidParameterException("HashingAlgorithm must not be null.");
        }
        if (digits < 1) {
            throw new InvalidParameterException("Number of digits must be higher than 0.");
        }
        this.algorithm = algorithm;
        this.digits = digits;
    }

    @Override
    public String generate(String key, long counter) throws CodeGenerationException {
        try {
            byte[] hash = this.generateHash(key, counter);
            return this.getDigitsFromHash(hash);
        }
        catch (Exception e) {
            throw new CodeGenerationException("Failed to generate code. See nested exception.", e);
        }
    }

    private byte[] generateHash(String key, long counter) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] data = new byte[8];
        long value = counter;
        int i = 8;
        while (i-- > 0) {
            data[i] = (byte)value;
            value >>>= 8;
        }
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(key);
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, this.algorithm.getHmacAlgorithm());
        Mac mac = Mac.getInstance(this.algorithm.getHmacAlgorithm());
        mac.init(signKey);
        return mac.doFinal(data);
    }

    private String getDigitsFromHash(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xF;
        long truncatedHash = 0L;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (long)(hash[offset + i] & 0xFF);
        }
        truncatedHash &= Integer.MAX_VALUE;
        truncatedHash = (long)((double)truncatedHash % Math.pow(10.0, this.digits));
        return String.format("%0" + this.digits + "d", truncatedHash);
    }
}

