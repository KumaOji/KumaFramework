/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.model.Callable;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.log.LogUtils;

public final class TimeWatchUtils {
    public static void print(boolean isPrint, String msg, Callable.Action0 action0) {
        TimeWatchUtils.print(isPrint, msg, () -> {
            action0.invoke();
            return 1;
        });
    }

    public static <T> T print(boolean isPrint, String msg, Callable.Func0<T> action0) {
        if (isPrint) {
            long b = System.currentTimeMillis();
            T t = action0.invoke();
            long e = System.currentTimeMillis();
            LogUtils.info(PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY) + "--" + msg + " \u8017\u65f6: {}, ", e - b + "\u6beb\u79d2");
            return t;
        }
        return action0.invoke();
    }
}

