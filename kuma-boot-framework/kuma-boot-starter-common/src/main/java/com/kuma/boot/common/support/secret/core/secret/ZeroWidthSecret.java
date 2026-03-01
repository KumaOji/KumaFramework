//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.utils.io.EncodeUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * ZeroWidthSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class ZeroWidthSecret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        String source = context.sourceText();
        char[] chars = EncodeUtils.encodeUnicode(source).toCharArray();
        StringBuilder result = new StringBuilder();
        StringBuilder jtext = new StringBuilder("\\u200E");

        for (char aChar : chars) {
            if (Integer.toBinaryString(aChar).length() == 7) {
                result.append("0");
                result.append(Integer.toBinaryString(aChar));
            } else {
                result.append("00");
                result.append(Integer.toBinaryString(aChar));
            }
        }

        for (int i = 0; i < result.toString().length(); ++i) {
            if (i % 2 == 0) {
                switch (result.toString().substring(i, i + 2)) {
                    case "00":
                        jtext.append("\\u200a");
                        break;
                    case "01":
                        jtext.append("\\u200b");
                        break;
                    case "10":
                        jtext.append("\\u200c");
                        break;
                    case "11":
                        jtext.append("\\u200d");
                }
            }
        }

        jtext.append("\\200E");
        String text = decodeUnicode(jtext.toString());
        return StringUtils.getBytes(text, context.charset());
    }

    private static String decodeUnicode( String dataStr ) {
        int start = 0;
        int end = 0;

        StringBuffer buffer;
        for (buffer = new StringBuffer(); start > -1; start = end) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }

            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(letter);
        }

        return buffer.toString();
    }

    public byte[] doDecrypt( SecretContext context ) {
        String source = context.sourceText();
        String text = EncodeUtils.encodeUnicode(source);
        String str = text.substring(text.indexOf("\\ufeff") + 6, text.lastIndexOf("\\ufeff"));
        str = str.replace("\\u200a", "00");
        str = str.replace("\\u200b", "01");
        str = str.replace("\\u200c", "10");
        str = str.replace("\\u200d", "11");
        char[] tempChar = new char[str.length() / 8];

        for (int i = 0; i < str.length(); ++i) {
            if (i % 8 == 0) {
                tempChar[i / 8] = this.binaryStringToChar(str.substring(i, i + 8));
            }
        }

        String result = EncodeUtils.decodeUnicode(String.valueOf(tempChar));
        return StringUtils.getBytes(result, context.charset());
    }

    private int[] binaryStringToIntArray( String binStr ) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];

        for (int i = 0; i < temp.length; ++i) {
            result[i] = temp[i] - 48;
        }

        return result;
    }

    private char binaryStringToChar( String binStr ) {
        int[] temp = this.binaryStringToIntArray(binStr);
        int sum = 0;

        for (int i = 0; i < temp.length; ++i) {
            sum += temp[temp.length - 1 - i] << i;
        }

        return (char) sum;
    }
}
