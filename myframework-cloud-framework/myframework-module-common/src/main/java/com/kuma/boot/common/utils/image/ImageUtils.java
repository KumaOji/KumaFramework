/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.image;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static BufferedImage zoomOut(BufferedImage src, int maxSize, ResizeMode resizeMode, boolean forceZoom) {
        int width = src.getWidth();
        int height = src.getHeight();
        int newWidth = 0;
        int newHeight = 0;
        boolean resize = false;
        if (resizeMode == ResizeMode.AUTO) {
            resizeMode = width >= height ? ResizeMode.FIX_WIDTH : ResizeMode.FIX_HEIGHT;
        }
        if (resizeMode == ResizeMode.FIX_WIDTH) {
            if (width > maxSize || forceZoom) {
                newWidth = maxSize;
                newHeight = Math.toIntExact((long)maxSize * (long)height / (long)width);
                resize = true;
            }
        } else if (resizeMode == ResizeMode.FIX_HEIGHT && (height > maxSize || forceZoom)) {
            newHeight = maxSize;
            newWidth = Math.toIntExact((long)maxSize * (long)width / (long)height);
            resize = true;
        }
        if (!resize) {
            return src;
        }
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, 1);
        newImage.getGraphics().drawImage(src.getScaledInstance(newWidth, newHeight, 1), 0, 0, null);
        return newImage;
    }

    public static byte[] zoomOut(InputStream in, int maxSize, ResizeMode resizeMode, boolean forceZoom) throws IOException {
        BufferedImage dist = ImageUtils.zoomOut(ImageIO.read(in), maxSize, resizeMode, forceZoom);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();){
            ImageIO.write((RenderedImage)dist, "jpg", out);
            byte[] byArray = out.toByteArray();
            return byArray;
        }
    }

    public static byte[] zoomOut(byte[] bytes, int maxSize, ResizeMode resizeMode, boolean forceZoom) throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);){
            byte[] byArray = ImageUtils.zoomOut(in, maxSize, resizeMode, forceZoom);
            return byArray;
        }
    }

    public static BufferedImage read(File input) {
        try {
            return ImageIO.read(input);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static BufferedImage read(InputStream input) {
        try {
            return ImageIO.read(input);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static BufferedImage read(String url) {
        return StringUtils.isHttpUrl(url) ? ImageUtils.readUrl(url) : ImageUtils.read(new File(url));
    }

    private static BufferedImage readUrl(String url) {
        try {
            return ImageIO.read(URL.of(URI.create(url), null));
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static BufferedImage read(URL url) {
        try {
            return ImageIO.read(url);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static boolean write(RenderedImage im, String formatName, ImageOutputStream output) {
        try {
            return ImageIO.write(im, formatName, output);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static boolean write(RenderedImage im, String formatName, File output) {
        try {
            return ImageIO.write(im, formatName, output);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static boolean write(RenderedImage im, String formatName, OutputStream output) {
        try {
            return ImageIO.write(im, formatName, output);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte[] writeAsBytes(RenderedImage im, String formatName) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();){
            if (!ImageIO.write(im, formatName, output)) throw new IllegalArgumentException("ImageWriter formatName " + formatName + " writer is null.");
            byte[] byArray = output.toByteArray();
            return byArray;
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static ByteArrayInputStream writeAsStream(RenderedImage im, String formatName) {
        return new ByteArrayInputStream(ImageUtils.writeAsBytes(im, formatName));
    }

    public static enum ResizeMode {
        FIX_WIDTH,
        FIX_HEIGHT,
        AUTO;

    }
}

