/*
 *  com.kuma.boot.common.support.expression.ExpressionResolver
 *  com.kuma.boot.common.support.expression.SpringExpressionResolver
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 */
package com.kuma.boot.ratelimit.ratelimitredisson;

import com.kuma.boot.common.support.expression.ExpressionResolver;
import com.kuma.boot.common.support.expression.SpringExpressionResolver;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ratelimit.ratelimitredisson.annotation.Limit;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class LimitSupport {
    private final ExpressionResolver resolver = new SpringExpressionResolver();
    private final LimitExecutor limitExecutor;

    public LimitSupport(LimitExecutor limitExecutor) {
        this.limitExecutor = limitExecutor;
    }

    public boolean exec(Limit limit, Method method, Object[] args) {
        String key = limit.key();
        int rate = limit.rate();
        int rateInterval = limit.rateInterval();
        TimeUnit unit = limit.rateIntervalUnit();
        Object spELKey = this.resolver.evaluate(key, method, args);
        if (limit.restrictIp()) {
            String ip = RequestUtils.getIpAddress();
            spELKey = (String)spELKey + "#" + ip;
        }
        return this.limitExecutor.tryAccess((String)spELKey, rate, Duration.ofMillis(unit.toMillis(rateInterval)));
    }
}

