/*
 *  org.springframework.cglib.core.ReflectUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.method;

import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.springframework.cglib.core.ReflectUtils;

public class RateLimitMethodService
implements IRateLimitMethodService {
    @Override
    public String getMethodId(Method method, Object[] params) {
        return ReflectUtils.getMethodInfo((Member)method).getSignature().getName();
    }
}

