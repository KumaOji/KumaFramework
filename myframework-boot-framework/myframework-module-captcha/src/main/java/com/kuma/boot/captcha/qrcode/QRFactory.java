/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.zxing.WriterException
 */
package com.kuma.boot.captcha.qrcode;

import com.google.zxing.WriterException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

public class QRFactory
implements QRParser {
    @Override
    public boolean generate(String text, String filePath, String fileName) throws WriterException, IOException {
        return false;
    }

    @Override
    public boolean generate(String text, String filePath, String fileName, BufferedImage image) {
        return false;
    }

    @Override
    public CharSequence parser(BufferedImage image) {
        return null;
    }

    @Override
    public CharSequence parser(String filePath, boolean isColorFul) throws Exception {
        return null;
    }

    public static QRParser getParser(ParserEnum parserEnum) throws OperationNotSupportedException {
        if (parserEnum == ParserEnum.ZXING) {
            return new ZXINGParser();
        }
        if (parserEnum == ParserEnum.BOOFCV) {
            return new BoofcvParser();
        }
        throw new OperationNotSupportedException("no supported this parser");
    }
}

