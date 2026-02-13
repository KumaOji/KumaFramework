/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class FailOverUtils {
    private static final String NAME = "\u8865\u507f\u5de5\u5177";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SafeVarargs
    public static <T> T invoke(Consumer<Result<T>> consumer, Callable<T> ... c1) {
        Result result = new Result();
        try {
            int times = 0;
            for (int i = 0; i < c1.length; ++i) {
                Callable<T> tCallable = c1[i];
                try {
                    result.response = tCallable.call();
                    result.success = true;
                    break;
                }
                catch (Exception e) {
                    result.throwable = e;
                    if (i > 0) {
                        LogUtils.error(NAME.concat("-\u5931\u8d25-\u8865\u507f\u6b21\u6570 {}") + " error info {}", i, ExceptionUtils.getFullStackTrace(e));
                    } else {
                        LogUtils.error(ExceptionUtils.getFullStackTrace(e), new Object[0]);
                    }
                    times = i + 1;
                    continue;
                }
            }
            if (result.success && times > 0) {
                LogUtils.info(PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY) + " {} \u8865\u507f\u6210\u529f, \u8865\u507f\u6b21\u6570\uff1a{}", NAME, times);
            }
        }
        finally {
            consumer.accept(result);
        }
        return (T) result.response;
    }

    public static class Result<T> {
        private boolean success = false;
        private Throwable throwable;
        private T response;

        public boolean isSuccess() {
            return this.success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public T getResponse() {
            return this.response;
        }

        public void setResponse(T response) {
            this.response = response;
        }
    }
}

