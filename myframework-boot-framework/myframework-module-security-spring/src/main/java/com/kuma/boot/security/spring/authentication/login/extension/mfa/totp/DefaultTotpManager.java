/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.totp.code.CodeGenerator
 *  com.kuma.boot.totp.code.CodeVerifier
 *  com.kuma.boot.totp.code.DefaultCodeGenerator
 *  com.kuma.boot.totp.code.DefaultCodeVerifier
 *  com.kuma.boot.totp.code.HashingAlgorithm
 *  com.kuma.boot.totp.exceptions.QrGenerationException
 *  com.kuma.boot.totp.qr.QrData
 *  com.kuma.boot.totp.qr.QrDataFactory
 *  com.kuma.boot.totp.qr.QrGenerator
 *  com.kuma.boot.totp.qr.ZxingPngQrGenerator
 *  com.kuma.boot.totp.secret.DefaultSecretGenerator
 *  com.kuma.boot.totp.secret.SecretGenerator
 *  com.kuma.boot.totp.time.SystemTimeProvider
 *  com.kuma.boot.totp.time.TimeProvider
 *  com.kuma.boot.totp.util.Utils
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.totp;

import com.kuma.boot.totp.code.CodeGenerator;
import com.kuma.boot.totp.code.CodeVerifier;
import com.kuma.boot.totp.code.DefaultCodeGenerator;
import com.kuma.boot.totp.code.DefaultCodeVerifier;
import com.kuma.boot.totp.code.HashingAlgorithm;
import com.kuma.boot.totp.exceptions.QrGenerationException;
import com.kuma.boot.totp.qr.QrData;
import com.kuma.boot.totp.qr.QrDataFactory;
import com.kuma.boot.totp.qr.QrGenerator;
import com.kuma.boot.totp.qr.ZxingPngQrGenerator;
import com.kuma.boot.totp.secret.DefaultSecretGenerator;
import com.kuma.boot.totp.secret.SecretGenerator;
import com.kuma.boot.totp.time.SystemTimeProvider;
import com.kuma.boot.totp.time.TimeProvider;
import com.kuma.boot.totp.util.Utils;

public class DefaultTotpManager
implements MfaAuthenticationManager {
    private static final int DEFAULT_SECRET_LENGTH = 64;
    private static final int DEFAULT_CODE_LENGTH = 6;
    private static final int DEFAULT_TIME_PERIOD = 30;
    private final QrDataFactory qrDataFactory = new QrDataFactory(HashingAlgorithm.SHA256, 6, 30);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
    private final CodeVerifier verifier;

    public DefaultTotpManager() {
        SystemTimeProvider timeProvider = new SystemTimeProvider();
        DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA256, 6);
        this.verifier = new DefaultCodeVerifier((CodeGenerator)codeGenerator, (TimeProvider)timeProvider);
    }

    @Override
    public String generateSecret() {
        return this.secretGenerator.generate();
    }

    @Override
    public boolean validCode(String secret, String code) {
        return this.verifier.isValidCode(secret, code);
    }

    @Override
    public String getUriForImage(String label, String secret, String issuer) throws QrGenerationException {
        QrData data = this.qrDataFactory.newBuilder().label(label).secret(secret).issuer(issuer).build();
        String qrCodeImage = Utils.getDataUriForImage((byte[])this.qrGenerator.generate(data), (String)this.qrGenerator.getImageMimeType());
        return qrCodeImage;
    }
}

