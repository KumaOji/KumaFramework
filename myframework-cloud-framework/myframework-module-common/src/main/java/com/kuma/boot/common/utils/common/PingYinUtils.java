/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Random;

public final class PingYinUtils {
    private PingYinUtils() {
    }

    public static String getPyIndexStr(String strChinese, boolean bUpCase) {
        try {
            StringBuilder buffer = new StringBuilder();
            byte[] chineseBytes = strChinese.getBytes("GBK");
            for (int i = 0; i < chineseBytes.length; ++i) {
                if ((chineseBytes[i] & 0xFF) > 128) {
                    int char1 = chineseBytes[i++] & 0xFF;
                    int chart = (char1 <<= 8) + (chineseBytes[i] & 0xFF);
                    buffer.append(PingYinUtils.getPyIndexChar((char)chart, bUpCase));
                    continue;
                }
                char c = (char)chineseBytes[i];
                if (!Character.isJavaIdentifierPart(c)) {
                    c = 'A';
                }
                buffer.append(c);
            }
            return buffer.toString();
        }
        catch (Exception e) {
            LogUtils.error("\u53d6\u4e2d\u6587\u62fc\u97f3\u6709\u9519", e);
            return null;
        }
    }

    private static char getPyIndexChar(char charGbk, boolean bUpCase) {
        int result = charGbk >= '\ub0a1' && charGbk <= '\ub0c4' ? 65 : (charGbk >= '\ub0c5' && charGbk <= '\ub2c0' ? 66 : (charGbk >= '\ub2c1' && charGbk <= '\ub4ed' ? 67 : (charGbk >= '\ub4ee' && charGbk <= '\ub6e9' ? 68 : (charGbk >= '\ub6ea' && charGbk <= '\ub7a1' ? 69 : (charGbk >= '\ub7a2' && charGbk <= '\ub8c0' ? 70 : (charGbk >= '\ub8c1' && charGbk <= '\ub9fd' ? 71 : (charGbk >= '\ub9fe' && charGbk <= '\ubbf6' ? 72 : (charGbk >= '\ubbf7' && charGbk <= '\ubfa5' ? 74 : (charGbk >= '\ubfa6' && charGbk <= '\uc0ab' ? 75 : (charGbk >= '\uc0ac' && charGbk <= '\uc2e7' ? 76 : (charGbk >= '\uc2e8' && charGbk <= '\uc4c2' ? 77 : (charGbk >= '\uc4c3' && charGbk <= '\uc5b5' ? 78 : (charGbk >= '\uc5b6' && charGbk <= '\uc5bd' ? 79 : (charGbk >= '\uc5be' && charGbk <= '\uc6d9' ? 80 : (charGbk >= '\uc6da' && charGbk <= '\uc8ba' ? 81 : (charGbk >= '\uc8bb' && charGbk <= '\uc8f5' ? 82 : (charGbk >= '\uc8f6' && charGbk <= '\ucbf9' ? 83 : (charGbk >= '\ucbfa' && charGbk <= '\ucdd9' ? 84 : (charGbk >= '\ucdda' && charGbk <= '\ucef3' ? 87 : (charGbk >= '\ucef4' && charGbk <= '\ud1b8' ? 88 : (charGbk >= '\ud1b9' && charGbk <= '\ud4d0' ? 89 : (charGbk >= '\ud4d1' && charGbk <= '\ud7f9' ? 90 : (int)((char)(65 + new Random().nextInt(25)))))))))))))))))))))))));
        if (!bUpCase) {
            result = Character.toLowerCase((char)result);
        }
        return (char)result;
    }
}

