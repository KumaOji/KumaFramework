/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.qrcode;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ParserUtils
extends ZXINGParser {
    protected static BufferedImage convertToGrayscale(BufferedImage colorImage) {
        BufferedImage grayscaleImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), 10);
        grayscaleImage.getGraphics().drawImage(colorImage, 0, 0, null);
        return grayscaleImage;
    }

    protected static BufferedImage swapColors(BufferedImage image) throws IOException {
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int colorThreshold;
                int blue;
                int green;
                int rgb = image.getRGB(x, y);
                int red = rgb >> 16 & 0xFF;
                if (ParserUtils.isColorWithinThreshold(red, green = rgb >> 8 & 0xFF, blue = rgb & 0xFF, 255, 255, 255, colorThreshold = 30)) {
                    image.setRGB(x, y, 0);
                    continue;
                }
                if (!ParserUtils.isColorWithinThreshold(red, green, blue, 0, 0, 0, colorThreshold)) continue;
                image.setRGB(x, y, 0xFFFFFF);
            }
        }
        ImageIO.write((RenderedImage)image, "jpg", new File("/Users/laoxue/Desktop/convert.jpg"));
        return ImageIO.read(new File("/Users/laoxue/Desktop/convert.jpg"));
    }

    private static boolean isColorWithinThreshold(int r1, int g1, int b1, int r2, int g2, int b2, int threshold) {
        int deltaR = Math.abs(r1 - r2);
        int deltaG = Math.abs(g1 - g2);
        int deltaB = Math.abs(b1 - b2);
        return deltaR <= threshold && deltaG <= threshold && deltaB <= threshold;
    }
}

