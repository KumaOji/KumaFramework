//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.qrcode;

import com.google.zxing.WriterException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface QRParser {
    boolean generate(String text, String filePath, String fileName) throws WriterException, IOException;

    boolean generate(String text, String filePath, String fileName, BufferedImage image);

    CharSequence parser(BufferedImage image);

    CharSequence parser(String filePath, boolean isColorFul) throws Exception;
}
