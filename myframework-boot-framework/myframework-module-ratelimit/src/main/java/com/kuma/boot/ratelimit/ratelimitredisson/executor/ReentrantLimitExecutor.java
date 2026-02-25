/*
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.ratelimit.ratelimitredisson.executor;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ratelimit.ratelimitredisson.LimitExecutor;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

public abstract class ReentrantLimitExecutor
implements LimitExecutor {
    public static final String ATTRIBUTE_NAME = "limit";

    protected abstract boolean reentrantTryAccess(String var1, int var2, Duration var3);

    @Override
    public boolean tryAccess(String compositeKey, int rate, Duration rateInterval) {
        Boolean bool;
        HttpServletRequest request = RequestUtils.getRequest();
        Object limitAttribute = request.getAttribute(ATTRIBUTE_NAME);
        if (limitAttribute instanceof Boolean && (bool = (Boolean)limitAttribute).booleanValue()) {
            return true;
        }
        boolean bool2 = this.reentrantTryAccess(compositeKey, rate, rateInterval);
        request.setAttribute(ATTRIBUTE_NAME, (Object)bool2);
        return bool2;
    }
}

