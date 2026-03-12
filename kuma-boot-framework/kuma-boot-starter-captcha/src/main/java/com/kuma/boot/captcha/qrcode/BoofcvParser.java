//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.qrcode;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.alg.fiducial.qrcode.QrCodeEncoder;
import boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage;
import boofcv.alg.fiducial.qrcode.QrCode.ErrorLevel;
import boofcv.factory.fiducial.ConfigQrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;

public class BoofcvParser implements QRParser {
    public boolean generate(String text, String filePath, String fileName) throws IOException {
        QrCode qr = (new QrCodeEncoder()).setError(ErrorLevel.M).addAutomatic(text).fixate();
        QrCodeGeneratorImage render = new QrCodeGeneratorImage(20);
        render.render(qr);
        BufferedImage image = ConvertBufferedImage.convertTo(render.getGray(), (BufferedImage)null);
        return ImageIO.write(image, "jpg", Files.newOutputStream(Paths.get(filePath + fileName)));
    }

    public boolean generate(String text, String filePath, String fileName, BufferedImage image) {
        return true;
    }

    public CharSequence parser(BufferedImage image) {
        return null;
    }

    public CharSequence parser(String filePath, boolean isColorFul) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        GrayU8 input = ConvertBufferedImage.convertFrom(image, (GrayU8)null);
        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode((ConfigQrCode)null, GrayU8.class);
        detector.process(input);
        List<QrCode> detections = detector.getDetections();
        StringBuilder sb = new StringBuilder();

        for(QrCode qr : detections) {
            sb.append(qr.message);
        }

        return sb.toString();
    }
}
