/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.qr;

import com.kuma.boot.totp.exceptions.QrGenerationException;

public interface QrGenerator {
    public String getImageMimeType();

    public byte[] generate(QrData var1) throws QrGenerationException;
}

