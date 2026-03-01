/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.code;

import com.kuma.boot.totp.exceptions.CodeGenerationException;
import com.kuma.boot.totp.time.TimeProvider;

public class DefaultCodeVerifier
implements CodeVerifier {
    private final CodeGenerator codeGenerator;
    private final TimeProvider timeProvider;
    private int timePeriod = 30;
    private int allowedTimePeriodDiscrepancy = 1;

    public DefaultCodeVerifier(CodeGenerator codeGenerator, TimeProvider timeProvider) {
        this.codeGenerator = codeGenerator;
        this.timeProvider = timeProvider;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setAllowedTimePeriodDiscrepancy(int allowedTimePeriodDiscrepancy) {
        this.allowedTimePeriodDiscrepancy = allowedTimePeriodDiscrepancy;
    }

    @Override
    public boolean isValidCode(String secret, String code) {
        long currentBucket = Math.floorDiv(this.timeProvider.getTime(), this.timePeriod);
        boolean success = false;
        for (int i = -this.allowedTimePeriodDiscrepancy; i <= this.allowedTimePeriodDiscrepancy; ++i) {
            success = this.checkCode(secret, currentBucket + (long)i, code) || success;
        }
        return success;
    }

    private boolean checkCode(String secret, long counter, String code) {
        try {
            String actualCode = this.codeGenerator.generate(secret, counter);
            return this.timeSafeStringComparison(actualCode, code);
        }
        catch (CodeGenerationException e) {
            return false;
        }
    }

    private boolean timeSafeStringComparison(String a, String b) {
        byte[] bBytes;
        byte[] aBytes = a.getBytes();
        if (aBytes.length != (bBytes = b.getBytes()).length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < aBytes.length; ++i) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}

