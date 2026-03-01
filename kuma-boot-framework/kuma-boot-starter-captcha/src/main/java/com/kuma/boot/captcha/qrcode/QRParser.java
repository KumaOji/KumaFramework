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

public interface QRParser {
    public boolean generate(String var1, String var2, String var3) throws WriterException, IOException;

    public boolean generate(String var1, String var2, String var3, BufferedImage var4);

    public CharSequence parser(BufferedImage var1);

    public CharSequence parser(String var1, boolean var2) throws Exception;
}

