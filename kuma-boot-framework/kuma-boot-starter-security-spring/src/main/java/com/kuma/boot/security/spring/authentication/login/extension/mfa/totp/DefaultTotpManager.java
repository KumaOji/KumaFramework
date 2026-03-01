/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.extension.mfa.totp;

import static com.kuma.boot.totp.util.Utils.getDataUriForImage;

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

/**
 * @author: ReLive27
 * @since: 2023/1/12 19:40
 */
public class DefaultTotpManager implements MfaAuthenticationManager {
    private static final int DEFAULT_SECRET_LENGTH = 64;
    private static final int DEFAULT_CODE_LENGTH = 6;
    private static final int DEFAULT_TIME_PERIOD = 30;
    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final SecretGenerator secretGenerator;
    private final CodeVerifier verifier;

    public DefaultTotpManager() {
        this.qrDataFactory =
                new QrDataFactory(
                        HashingAlgorithm.SHA256, DEFAULT_CODE_LENGTH, DEFAULT_TIME_PERIOD);
        this.qrGenerator = new ZxingPngQrGenerator();
        this.secretGenerator = new DefaultSecretGenerator(DEFAULT_SECRET_LENGTH);
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator =
                new DefaultCodeGenerator(HashingAlgorithm.SHA256, DEFAULT_CODE_LENGTH);
        this.verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
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
    public String getUriForImage(String label, String secret, String issuer)
            throws QrGenerationException {
        QrData data = qrDataFactory.newBuilder().label(label).secret(secret).issuer(issuer).build();

        // Generate the QR code image data as a base64 string which
        // can be used in an <img> tag:
        String qrCodeImage =
                getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
        return qrCodeImage;
    }
}
