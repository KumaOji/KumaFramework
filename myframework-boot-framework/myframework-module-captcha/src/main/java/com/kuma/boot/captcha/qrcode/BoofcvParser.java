/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  boofcv.abst.fiducial.QrCodePreciseDetector
 *  boofcv.alg.fiducial.qrcode.QrCode
 *  boofcv.alg.fiducial.qrcode.QrCode$ErrorLevel
 *  boofcv.alg.fiducial.qrcode.QrCodeEncoder
 *  boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage
 *  boofcv.factory.fiducial.FactoryFiducial
 *  boofcv.io.image.ConvertBufferedImage
 *  boofcv.struct.image.GrayU8
 *  boofcv.struct.image.ImageGray
 */
package com.kuma.boot.captcha.qrcode;

import boofcv.abst.fiducial.QrCodePreciseDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.alg.fiducial.qrcode.QrCodeEncoder;
import boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageGray;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;

public class BoofcvParser
implements QRParser {
    @Override
    public boolean generate(String text, String filePath, String fileName) throws IOException {
        QrCode qr = new QrCodeEncoder().setError(QrCode.ErrorLevel.M).addAutomatic(text).fixate();
        QrCodeGeneratorImage render = new QrCodeGeneratorImage(20);
        render.render(qr);
        BufferedImage image = ConvertBufferedImage.convertTo((GrayU8)render.getGray(), null);
        return ImageIO.write((RenderedImage)image, "jpg", Files.newOutputStream(Paths.get(filePath + fileName, new String[0]), new OpenOption[0]));
    }

    @Override
    public boolean generate(String text, String filePath, String fileName, BufferedImage image) {
        return true;
    }

    @Override
    public CharSequence parser(BufferedImage image) {
        return null;
    }

    @Override
    public CharSequence parser(String filePath, boolean isColorFul) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        GrayU8 input = ConvertBufferedImage.convertFrom((BufferedImage)image, (GrayU8)null);
        QrCodePreciseDetector detector = FactoryFiducial.qrcode(null, GrayU8.class);
        detector.process((ImageGray)input);
        List detections = detector.getDetections();
        StringBuilder sb = new StringBuilder();
        for (QrCode qr : detections) {
            sb.append(qr.message);
        }
        return sb.toString();
    }
}

