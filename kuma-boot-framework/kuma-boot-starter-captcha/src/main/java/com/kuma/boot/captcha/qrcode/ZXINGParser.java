//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public class ZXINGParser implements QRParser {
    public boolean generate(String text, String filePath, String fileName) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 1300, 1300, hintMap);
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth - 200, matrixWidth - 200, 1);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        graphics.setColor(Color.BLACK);

        for(int i = 0; i < matrixWidth; ++i) {
            for(int j = 0; j < matrixWidth; ++j) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i - 100, j - 100, 1, 1);
                }
            }
        }

        return ImageIO.write(image, "jpg", Files.newOutputStream(Paths.get(filePath + fileName)));
    }

    public boolean generate(String text, String filePath, String fileName, BufferedImage image) {
        return false;
    }

    public CharSequence parser(BufferedImage image) {
        return null;
    }

    public CharSequence parser(String filePath, boolean isColorFul) throws Exception {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            HashMap<DecodeHintType, Object> hints = new HashMap();
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException | IOException var9) {
            throw new IOException("parse fail");
        }
    }

    public String parseImage(String filePath) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            BufferedImage bufferedImage = ParserUtils.convertToGrayscale(image);
            bufferedImage = ParserUtils.swapColors(bufferedImage);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            HashMap<DecodeHintType, Object> hints = new HashMap();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException | IOException var9) {
            throw new IOException("parse fail");
        }
    }
}
