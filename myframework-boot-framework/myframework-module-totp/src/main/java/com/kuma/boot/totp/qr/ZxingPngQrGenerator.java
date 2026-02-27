/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.zxing.BarcodeFormat
 *  com.google.zxing.Writer
 *  com.google.zxing.client.j2se.MatrixToImageWriter
 *  com.google.zxing.common.BitMatrix
 *  com.google.zxing.qrcode.QRCodeWriter
 */
package com.kuma.boot.totp.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kuma.boot.totp.exceptions.QrGenerationException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class ZxingPngQrGenerator
implements QrGenerator {
    private final Writer writer;
    private int imageSize = 350;

    public ZxingPngQrGenerator() {
        this((Writer)new QRCodeWriter());
    }

    public ZxingPngQrGenerator(Writer writer) {
        this.writer = writer;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public int getImageSize() {
        return this.imageSize;
    }

    @Override
    public String getImageMimeType() {
        return "image/png";
    }

    @Override
    public byte[] generate(QrData data) throws QrGenerationException {
        try {
            BitMatrix bitMatrix = this.writer.encode(data.getUri(), BarcodeFormat.QR_CODE, this.imageSize, this.imageSize);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream((BitMatrix)bitMatrix, (String)"PNG", (OutputStream)pngOutputStream);
            return pngOutputStream.toByteArray();
        }
        catch (Exception e) {
            throw new QrGenerationException("Failed to generate QR code. See nested exception.", e);
        }
    }
}

