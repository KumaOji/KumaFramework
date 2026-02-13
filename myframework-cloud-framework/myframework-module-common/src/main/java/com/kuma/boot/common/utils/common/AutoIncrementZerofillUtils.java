/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.CharUtil
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.common.utils.common;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.lang.StringUtils;

public class AutoIncrementZerofillUtils {
    public static String getInitValue(int length) {
        return StrUtil.padPre((CharSequence)"1", (int)length, (char)'0');
    }

    public static String autoIncrement(String str) {
        int maxIndex = str.length() - 1;
        int autoIncrementValue = Integer.parseInt(CharUtil.toString((char)str.charAt(maxIndex))) + 1;
        if (autoIncrementValue == 10) {
            int cycleIndex = 0;
            for (int i = maxIndex - 1; i >= 0; --i) {
                int autoIncrementValueI = Integer.parseInt(CharUtil.toString((char)str.charAt(i))) + 1;
                ++cycleIndex;
                if (autoIncrementValueI == 10) continue;
                String pad = StrUtil.padPre((CharSequence)"0", (int)cycleIndex, (char)'0');
                String replaceValue = Integer.toString(autoIncrementValueI) + pad;
                return StringUtils.replace(str, replaceValue, i, i + 1 + replaceValue.length());
            }
            throw new BaseException("\u65e0\u6cd5\u81ea\u52a8\u9012\u589e\uff0c\u6b64\u53c2\u6570\u5df2\u662f\u6700\u5927\u503c\uff1a" + str);
        }
        return str.substring(0, maxIndex) + autoIncrementValue;
    }

    public static String autoDecr(String str) {
        int maxIndex = str.length() - 1;
        int autoDecrValue = Integer.parseInt(CharUtil.toString((char)str.charAt(maxIndex))) - 1;
        if (autoDecrValue == -1) {
            int cycleIndex = 0;
            for (int i = maxIndex - 1; i >= 0; --i) {
                int autoDecrValueI = Integer.parseInt(CharUtil.toString((char)str.charAt(i))) - 1;
                ++cycleIndex;
                if (autoDecrValueI == -1) continue;
                String pad = StrUtil.padPre((CharSequence)"9", (int)cycleIndex, (char)'9');
                String replaceValue = Integer.toString(autoDecrValueI) + pad;
                return StringUtils.replace(str, replaceValue, i, i + 1 + replaceValue.length());
            }
            throw new BaseException("\u65e0\u6cd5\u81ea\u52a8\u9012\u51cf\uff0c\u6b64\u53c2\u6570\u5df2\u662f\u6700\u5c0f\u503c\uff1a" + str);
        }
        return str.substring(0, maxIndex) + autoDecrValue;
    }
}

