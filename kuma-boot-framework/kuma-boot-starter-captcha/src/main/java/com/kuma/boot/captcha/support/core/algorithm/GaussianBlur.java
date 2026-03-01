/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.algorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class GaussianBlur {
    public static void execute(BufferedImage image, int pixelX, int pixelY, int[][] matrix, int[] values, int size) {
        GaussianBlur.readPixel(image, pixelX, pixelY, values, size);
        GaussianBlur.fillMatrix(matrix, values);
        image.setRGB(pixelX, pixelY, GaussianBlur.averageMatrix(matrix));
    }

    private static void readPixel(BufferedImage bufferedImage, int x, int y, int[] pixels, int size) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < size + xStart; ++i) {
            for (int j = yStart; j < size + yStart; ++j) {
                int tempX = GaussianBlur.calculatePixel(x, i, bufferedImage.getWidth());
                int tempY = GaussianBlur.calculatePixel(y, j, bufferedImage.getHeight());
                pixels[current++] = bufferedImage.getRGB(tempX, tempY);
            }
        }
    }

    private static int calculatePixel(int value, int step, int bound) {
        int pixel = step;
        if (pixel < 0) {
            pixel = -pixel;
        } else if (pixel >= bound) {
            pixel = value;
        }
        return pixel;
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; ++j) {
                x[j] = values[filled++];
            }
        }
    }

    private static int averageMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; ++j) {
                if (j == 1) continue;
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }
}

