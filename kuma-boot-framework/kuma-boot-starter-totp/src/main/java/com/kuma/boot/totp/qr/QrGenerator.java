package com.kuma.boot.totp.qr;

import com.kuma.boot.totp.exceptions.QrGenerationException;

public interface QrGenerator {
    /**
     * @return The mime type of the image that the generator generates, e.g. image/png
     */
    String getImageMimeType();

    /**
     * @param data The QrData object to encode in the generated image.
     * @return The raw image data as a byte array.
     * @throws QrGenerationException thrown if image generation fails for any reason.
     */
    byte[] generate(com.kuma.boot.totp.qr.QrData data) throws QrGenerationException;
}
