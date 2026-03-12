//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.qrcode;

import com.google.zxing.WriterException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

public class QRFactory implements QRParser {
    public boolean generate(String text, String filePath, String fileName) throws WriterException, IOException {
        return false;
    }

    public boolean generate(String text, String filePath, String fileName, BufferedImage image) {
        return false;
    }

    public CharSequence parser(BufferedImage image) {
        return null;
    }

    public CharSequence parser(String filePath, boolean isColorFul) throws Exception {
        return null;
    }

    public static QRParser getParser(ParserEnum parserEnum) throws OperationNotSupportedException {
        if (parserEnum == ParserEnum.ZXING) {
            return new ZXINGParser();
        } else if (parserEnum == ParserEnum.BOOFCV) {
            return new BoofcvParser();
        } else {
            throw new OperationNotSupportedException("no supported this parser");
        }
    }
}
