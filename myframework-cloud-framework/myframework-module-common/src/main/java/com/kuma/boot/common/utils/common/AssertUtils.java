/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.lang.Assert
 */
package com.kuma.boot.common.utils.common;

import cn.hutool.core.lang.Assert;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;

public class AssertUtils
extends Assert {
    public static void notThrow(String throwMsg, Runnable lambdaRun) {
        try {
            lambdaRun.run();
        }
        catch (Exception e) {
            throw new BaseException(throwMsg);
        }
    }

    public static void notThrowIfErrorPrintMsg(String printMsg, Runnable lambdaRun) {
        try {
            lambdaRun.run();
        }
        catch (Exception e) {
            LogUtils.warn(printMsg, new Object[0]);
        }
    }

    public static void notThrowIfErrorPrintStackTrace(String printMsg, Runnable lambdaRun) {
        try {
            lambdaRun.run();
        }
        catch (Exception e) {
            LogUtils.warn(printMsg, new Object[0]);
            LogUtils.error(e);
        }
    }
}

