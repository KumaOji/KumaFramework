/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.image;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

/**
 * 图片工具类
 *
 */
public final class ImageUtils {

    private ImageUtils() {}

    /**
     * 调整模式
     */
    public enum ResizeMode {

        /**
         * 固定宽
         */
        FIX_WIDTH,

        /**
         * 固定高
         */
        FIX_HEIGHT,

        /**
         * 自动
         */
        AUTO;
    }

    /**
     * 缩小图片
     * @param src 原图片
     * @param maxSize 最大尺寸
     * @param resizeMode 调整模式
     * @param forceZoom 是否强制缩放（true表示本来没有那么大 强制撑到那么大）
     * @return 缩小后的图片
     */
    public static BufferedImage zoomOut(
            BufferedImage src, int maxSize, ResizeMode resizeMode, boolean forceZoom) {
        int width = src.getWidth(), height = src.getHeight(), newWidth = 0, newHeight = 0;

        // 是否需要调整 当图片尺寸不足并且不需要强制缩放时 不需要调整 直接返回原图
        boolean resize = false;

        // 如果是自动的 横图（宽>高）时固定宽 竖图（高>宽）时固定高
        if (resizeMode == ResizeMode.AUTO) {
            if (width >= height) {
                resizeMode = ResizeMode.FIX_WIDTH;
            } else {
                resizeMode = ResizeMode.FIX_HEIGHT;
            }
        }

        if (resizeMode == ResizeMode.FIX_WIDTH) {
            // 固定宽时 新高 / 新宽 = 原高 / 原宽 => 新高 = 新宽 * 原高 / 原宽
            if (width > maxSize || forceZoom) {
                newWidth = maxSize;
                newHeight = Math.toIntExact((long) maxSize * height / width);
                resize = true;
            }
        } else if (resizeMode == ResizeMode.FIX_HEIGHT) {
            // 固定高时 新宽 / 新高 = 原宽 / 原高 => 新宽 = 新高 * 原宽 / 原高
            if (height > maxSize || forceZoom) {
                newHeight = maxSize;
                newWidth = Math.toIntExact((long) maxSize * width / height);
                resize = true;
            }
        }

        // 不需要调整时直接返回原图
        if (!resize) {
            return src;
        }

        // 调整为新的宽高
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        newImage.getGraphics()
                .drawImage(
                        src.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT),
                        0,
                        0,
                        null);
        return newImage;
    }

    /**
     * 缩小图片
     * @see ImageUtils#zoomOut(BufferedImage, int, ResizeMode, boolean)
     * @param in 输入流
     * @param maxSize 最大尺寸
     * @param resizeMode 调整模式
     * @param forceZoom 是否强制缩放（true表示本来没有那么大 强制撑到那么大）
     * @return 缩小后的图片
     */
    public static byte[] zoomOut(
            InputStream in, int maxSize, ResizeMode resizeMode, boolean forceZoom)
            throws IOException {
        BufferedImage dist = zoomOut(ImageIO.read(in), maxSize, resizeMode, forceZoom);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(dist, "jpg", out);
            return out.toByteArray();
        }
    }

    /**
     * 缩小图片
     * @see ImageUtils#zoomOut(BufferedImage, int, ResizeMode, boolean)
     * @param bytes 原图片
     * @param maxSize 最大尺寸
     * @param resizeMode 调整模式
     * @param forceZoom 是否强制缩放（true表示本来没有那么大 强制撑到那么大）
     * @return 缩小后的图片
     */
    public static byte[] zoomOut(
            byte[] bytes, int maxSize, ResizeMode resizeMode, boolean forceZoom)
            throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return zoomOut(in, maxSize, resizeMode, forceZoom);
        }
    }


    /**
     * 读取图片
     * @param input 图片文件
     * @return BufferedImage
     */
    public static BufferedImage read( File input) {
        try {
            return ImageIO.read(input);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取图片
     * @param input 图片文件流
     * @return BufferedImage
     */
    public static BufferedImage read(InputStream input) {
        try {
            return ImageIO.read(input);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取图片，http 或者 file 地址
     * @param url 图片链接地址
     * @return BufferedImage
     */
    public static BufferedImage read(String url) {
        return StringUtils.isHttpUrl(url) ? readUrl(url) : read(new File(url));
    }

    /**
     * 读取图片
     * @param url 图片链接地址
     * @return BufferedImage
     */
    private static BufferedImage readUrl(String url) {
        try {
            return ImageIO.read(URL.of(URI.create(url), null));
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取图片
     * @param url 图片链接地址
     * @return BufferedImage
     */
    public static BufferedImage read(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 写出图片
     * @param im RenderedImage to be written.
     * @param formatName a String containing the informal name of the format.
     * @param output an ImageOutputStream to be written to.
     * @return false if no appropriate writer is found.
     */
    public static boolean write( RenderedImage im, String formatName, ImageOutputStream output) {
        try {
            return ImageIO.write(im, formatName, output);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 写出图片
     * @param im RenderedImage to be written.
     * @param formatName a String containing the informal name of the format.
     * @param output an ImageOutputStream to be written to.
     * @return false if no appropriate writer is found.
     */
    public static boolean write(RenderedImage im, String formatName, File output) {
        try {
            return ImageIO.write(im, formatName, output);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 写出图片
     * @param im RenderedImage to be written.
     * @param formatName a String containing the informal name of the format.
     * @param output an ImageOutputStream to be written to.
     * @return false if no appropriate writer is found.
     */
    public static boolean write(RenderedImage im, String formatName, OutputStream output) {
        try {
            return ImageIO.write(im, formatName, output);
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 写出图片为 byte 数组
     * @param im RenderedImage to be written.
     * @param formatName a String containing the informal name of the format.
     * @return false if no appropriate writer is found.
     */
    public static byte[] writeAsBytes(RenderedImage im, String formatName) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (ImageIO.write(im, formatName, output)) {
                return output.toByteArray();
            }
            throw new IllegalArgumentException(
                    "ImageWriter formatName " + formatName + " writer is null.");
        } catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 写出图片为 InputStream
     * @param im RenderedImage to be written.
     * @param formatName a String containing the informal name of the format.
     * @return false if no appropriate writer is found.
     */
    public static ByteArrayInputStream writeAsStream(RenderedImage im, String formatName) {
        return new ByteArrayInputStream(writeAsBytes(im, formatName));
    }
}
