/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.zxing.BarcodeFormat
 *  com.google.zxing.Binarizer
 *  com.google.zxing.BinaryBitmap
 *  com.google.zxing.ChecksumException
 *  com.google.zxing.DecodeHintType
 *  com.google.zxing.EncodeHintType
 *  com.google.zxing.FormatException
 *  com.google.zxing.LuminanceSource
 *  com.google.zxing.NotFoundException
 *  com.google.zxing.Result
 *  com.google.zxing.WriterException
 *  com.google.zxing.client.j2se.BufferedImageLuminanceSource
 *  com.google.zxing.common.BitMatrix
 *  com.google.zxing.common.HybridBinarizer
 *  com.google.zxing.qrcode.QRCodeReader
 *  com.google.zxing.qrcode.QRCodeWriter
 *  com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
 *  com.kuma.boot.common.utils.exception.ExceptionUtils
 *  com.kuma.boot.common.utils.image.ImageUtils
 *  com.kuma.boot.common.utils.secure.Base64Utils
 */
package com.kuma.boot.captcha.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.image.ImageUtils;
import com.kuma.boot.common.utils.secure.Base64Utils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;

public final class QrCodeUtils {
    private final String content;
    private int size;
    private Charset encode;
    private ErrorCorrectionLevel errorCorrectionLevel;
    private double errorCorrectionLevelValue;
    private Color foreGroundColor;
    private Color backGroundColor;
    private String imageFormat;
    private boolean deleteMargin;
    private final Hashtable<EncodeHintType, Object> hints;
    private BufferedImage logo;

    private QrCodeUtils(String content) {
        this.content = content;
        this.size = 512;
        this.encode = StandardCharsets.UTF_8;
        this.errorCorrectionLevel = ErrorCorrectionLevel.M;
        this.errorCorrectionLevelValue = 0.15;
        this.foreGroundColor = Color.BLACK;
        this.backGroundColor = Color.WHITE;
        this.imageFormat = "png";
        this.deleteMargin = true;
        this.hints = new Hashtable();
    }

    public static QrCodeUtils form(String content) {
        return new QrCodeUtils(content);
    }

    public QrCodeUtils size(int size) {
        this.size = size;
        return this;
    }

    public QrCodeUtils encode(Charset encode) {
        if (null != encode) {
            this.encode = encode;
        }
        return this;
    }

    public QrCodeUtils errorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
        switch (errorCorrectionLevel) {
            case L: {
                this.errorCorrectionLevel = errorCorrectionLevel;
                this.errorCorrectionLevelValue = 0.07;
                break;
            }
            case M: {
                this.errorCorrectionLevel = errorCorrectionLevel;
                this.errorCorrectionLevelValue = 0.15;
                break;
            }
            case Q: {
                this.errorCorrectionLevel = errorCorrectionLevel;
                this.errorCorrectionLevelValue = 0.25;
                break;
            }
            case H: {
                this.errorCorrectionLevel = errorCorrectionLevel;
                this.errorCorrectionLevelValue = 0.3;
                break;
            }
            default: {
                this.errorCorrectionLevel = ErrorCorrectionLevel.M;
                this.errorCorrectionLevelValue = 0.15;
            }
        }
        return this;
    }

    public QrCodeUtils foreGroundColor(String foreGroundColor) {
        try {
            this.foreGroundColor = QrCodeUtils.getColor(foreGroundColor);
        }
        catch (NumberFormatException e) {
            this.foreGroundColor = Color.BLACK;
        }
        return this;
    }

    public QrCodeUtils foreGroundColor(Color foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
        return this;
    }

    public QrCodeUtils backGroundColor(String backGroundColor) {
        try {
            this.backGroundColor = QrCodeUtils.getColor(backGroundColor);
        }
        catch (NumberFormatException e) {
            this.backGroundColor = Color.WHITE;
        }
        return this;
    }

    public QrCodeUtils backGroundColor(Color backGroundColor) {
        this.backGroundColor = backGroundColor;
        return this;
    }

    public QrCodeUtils imageFormat(String imageFormat) {
        if (imageFormat != null) {
            this.imageFormat = imageFormat.toLowerCase();
        }
        return this;
    }

    public QrCodeUtils deleteMargin(boolean deleteMargin) {
        this.deleteMargin = deleteMargin;
        return this;
    }

    public Hashtable<EncodeHintType, ?> getHints() {
        this.hints.clear();
        this.hints.put(EncodeHintType.ERROR_CORRECTION, this.errorCorrectionLevel);
        this.hints.put(EncodeHintType.CHARACTER_SET, this.encode);
        this.hints.put(EncodeHintType.MARGIN, 0);
        return this.hints;
    }

    public QrCodeUtils logo(BufferedImage logo) {
        this.logo = logo;
        return this;
    }

    public QrCodeUtils logo(File logo) {
        return this.logo(ImageUtils.read((File)logo));
    }

    public QrCodeUtils logo(URL url) {
        return this.logo(ImageUtils.read((URL)url));
    }

    public QrCodeUtils logo(String iconPath) {
        return this.logo(ImageUtils.read((String)iconPath));
    }

    public QrCodeUtils logo(InputStream logoStream) {
        return this.logo(ImageUtils.read((InputStream)logoStream));
    }

    public boolean write(OutputStream output) {
        BufferedImage bufferedImage = this.toImage();
        return ImageUtils.write((RenderedImage)bufferedImage, (String)this.imageFormat, (OutputStream)output);
    }

    public File toFile(String f) {
        return this.toFile(new File(f));
    }

    public File toFile(File qrCodeFile) {
        if (!qrCodeFile.exists()) {
            qrCodeFile.getParentFile().mkdirs();
        }
        BufferedImage bufferedImage = this.toImage();
        ImageUtils.write((RenderedImage)bufferedImage, (String)this.imageFormat, (File)qrCodeFile);
        return qrCodeFile;
    }

    public String toBase64() {
        return "data:image/png;base64," + Base64Utils.encodeToString((byte[])this.toBytes());
    }

    public byte[] toBytes() {
        BufferedImage bufferedImage = this.toImage();
        return ImageUtils.writeAsBytes((RenderedImage)bufferedImage, (String)this.imageFormat);
    }

    public ByteArrayInputStream toStream() {
        BufferedImage bufferedImage = this.toImage();
        return ImageUtils.writeAsStream((RenderedImage)bufferedImage, (String)this.imageFormat);
    }

    public BufferedImage toImage() {
        BitMatrix matrix;
        try {
            matrix = new QRCodeWriter().encode(this.content, BarcodeFormat.QR_CODE, this.size, this.size, this.getHints());
        }
        catch (WriterException e) {
            throw ExceptionUtils.unchecked((Throwable)e);
        }
        if (this.deleteMargin) {
            matrix = QrCodeUtils.deleteWhite(matrix);
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int fgColor = this.foreGroundColor.getRGB();
        int bgColor = this.backGroundColor.getRGB();
        BufferedImage image = new BufferedImage(width, height, 5);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                image.setRGB(x, y, matrix.get(x, y) ? fgColor : bgColor);
            }
        }
        if (null != this.logo) {
            QrCodeUtils.addLogo(image, this.logo, this);
        }
        return image;
    }

    public static String read(String qrCodeFile) {
        return QrCodeUtils.read(ImageUtils.read((String)qrCodeFile));
    }

    public static String read(File qrCodeFile) {
        return QrCodeUtils.read(ImageUtils.read((File)qrCodeFile));
    }

    public static String read(URL qrCodeUrl) {
        return QrCodeUtils.read(ImageUtils.read((URL)qrCodeUrl));
    }

    public static String read(BufferedImage qrCodeImage) {
        return QrCodeUtils.read(qrCodeImage, (Map)null);
    }

    public static String read(String qrCodeFile, Charset encode) {
        return QrCodeUtils.read(ImageUtils.read((String)qrCodeFile), encode);
    }

    public static String read(File qrCodeFile, Charset encode) {
        return QrCodeUtils.read(ImageUtils.read((File)qrCodeFile), encode);
    }

    public static String read(URL qrCodeUrl, Charset encode) {
        return QrCodeUtils.read(ImageUtils.read((URL)qrCodeUrl), encode);
    }

    public static String read(BufferedImage qrCodeImage, Charset encode) {
        Hashtable<DecodeHintType, Charset> hints = new Hashtable<DecodeHintType, Charset>();
        hints.put(DecodeHintType.CHARACTER_SET, encode);
        return QrCodeUtils.read(qrCodeImage, hints);
    }

    public static String read(BufferedImage qrCodeImage, Map<DecodeHintType, ?> hints) {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(qrCodeImage);
        BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer((LuminanceSource)source));
        try {
            Result result = new QRCodeReader().decode(bitmap, hints);
            String string = result.getText();
            return string;
        }
        catch (ChecksumException | FormatException | NotFoundException e) {
            throw ExceptionUtils.unchecked((Throwable)e);
        }
        finally {
            qrCodeImage.getGraphics().dispose();
        }
    }

    public static byte[] readRawBytes(String qrCodeFile) {
        return QrCodeUtils.readRawBytes(ImageUtils.read((String)qrCodeFile));
    }

    public static byte[] readRawBytes(File qrCodeFile) {
        return QrCodeUtils.readRawBytes(ImageUtils.read((File)qrCodeFile));
    }

    public static byte[] readRawBytes(URL qrCodeUrl) {
        return QrCodeUtils.readRawBytes(ImageUtils.read((URL)qrCodeUrl));
    }

    public static byte[] readRawBytes(BufferedImage qrCodeImage) {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(qrCodeImage);
        BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer((LuminanceSource)source));
        try {
            Result result = new QRCodeReader().decode(bitmap);
            byte[] byArray = result.getRawBytes();
            return byArray;
        }
        catch (ChecksumException | FormatException | NotFoundException e) {
            throw ExceptionUtils.unchecked((Throwable)e);
        }
        finally {
            qrCodeImage.getGraphics().dispose();
        }
    }

    private static void addLogo(BufferedImage qrCodeImage, BufferedImage logoImage, QrCodeUtils qrCode) {
        int baseWidth = qrCodeImage.getWidth();
        int baseHeight = qrCodeImage.getHeight();
        int maxWidth = (int)Math.sqrt((double)(baseWidth * baseHeight) * qrCode.errorCorrectionLevelValue * 0.4);
        int logoRectWidth = Math.min(maxWidth, logoImage.getWidth());
        int logoRectHeight = Math.min(maxWidth, logoImage.getHeight());
        BufferedImage logoRect = new BufferedImage(logoRectWidth, logoRectHeight, 2);
        Graphics2D g2 = logoRect.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, logoRectWidth, logoRectHeight);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(2, 2, logoRectWidth - 4, logoRectHeight - 4);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(logoImage, 4, 4, logoRectWidth - 8, logoRectHeight - 8, null);
        logoImage.getGraphics().dispose();
        g2.dispose();
        Graphics2D gc = (Graphics2D)qrCodeImage.getGraphics();
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gc.setColor(qrCode.backGroundColor);
        gc.drawImage((Image)logoRect, (baseWidth - logoRectWidth) / 2, (baseHeight - logoRectHeight) / 2, null);
        gc.dispose();
    }

    private static Color getColor(String hexString) {
        if ('#' == hexString.charAt(0)) {
            return new Color(Long.decode(hexString).intValue());
        }
        return new Color(Long.decode("0xFF" + hexString).intValue());
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; ++i) {
            for (int j = 0; j < resHeight; ++j) {
                if (!matrix.get(i + rec[0], j + rec[1])) continue;
                resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }
}

