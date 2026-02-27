/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.captcha.qrcode;

import com.kuma.boot.common.utils.log.LogUtils;

public class Test
extends QRFactory {
    public static void main(String[] args) throws Exception {
        ZXINGParser zxingParser = (ZXINGParser)QRFactory.getParser(ParserEnum.ZXING);
        BoofcvParser boofcvParser = (BoofcvParser)QRFactory.getParser(ParserEnum.BOOFCV);
        zxingParser.generate("https//997coder.com", "/Users/laoxue/qrcoder/", "a.jpg");
        CharSequence content = zxingParser.parser("/Users/laoxue/qrcoder/a.jpg", false);
        LogUtils.info((String)"content: {}", (Object[])new Object[]{content});
        LogUtils.info((String)zxingParser.parseImage("1.jpg"), (Object[])new Object[0]);
    }
}

