//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.qrcode;

import com.kuma.boot.common.utils.log.LogUtils;

public class Test extends QRFactory {
    public static void main(String[] args) throws Exception {
        ZXINGParser zxingParser = (ZXINGParser)QRFactory.getParser(ParserEnum.ZXING);
        BoofcvParser boofcvParser = (BoofcvParser)QRFactory.getParser(ParserEnum.BOOFCV);
        zxingParser.generate("https//997coder.com", "/Users/laoxue/qrcoder/", "a.jpg");
        CharSequence content = zxingParser.parser("/Users/laoxue/qrcoder/a.jpg", false);
        LogUtils.info("content: {}", new Object[]{content});
        LogUtils.info(zxingParser.parseImage("1.jpg"), new Object[0]);
    }
}
