/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse;

import com.kuma.boot.web.gracefulresponse.api.AssertFunction;

public class GracefulResponse {
    public static void raiseException(String code, String msg) {
        throw new GracefulResponseException(code, msg);
    }

    public static void raiseException(String code, String msg, Throwable throwable) {
        throw new GracefulResponseException(code, msg, throwable);
    }

    public static void wrapAssert(AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        }
        catch (Exception e) {
            throw new GracefulResponseException(e.getMessage(), e);
        }
    }

    public static void wrapAssert(String code, AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        }
        catch (Exception e) {
            throw new GracefulResponseException(code, e.getMessage(), e);
        }
    }
}

