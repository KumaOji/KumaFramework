/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Arrays;

public final class MaskUtils {
    private MaskUtils() {
    }

    public static String mask(String source, int frontRetain, int rearRetain, char masker) {
        if (StringUtils.isEmpty(source)) {
            return source;
        }
        int len = source.length();
        int maskLength = len - (frontRetain + rearRetain);
        if (maskLength <= 0) {
            return source;
        }
        char[] masks = new char[maskLength];
        Arrays.fill(masks, masker);
        char[] chars = source.toCharArray();
        System.arraycopy(masks, 0, chars, frontRetain, maskLength);
        return new String(chars);
    }
}

