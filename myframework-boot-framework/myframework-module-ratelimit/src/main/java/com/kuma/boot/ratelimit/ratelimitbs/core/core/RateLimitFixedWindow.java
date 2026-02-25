/*
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;

public class RateLimitFixedWindow
extends AbstractRateLimit {
    @Override
    protected boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext rateLimitContext) {
        long configCount;
        long cacheCount;
        long newCount;
        ICommonCacheService cacheService = rateLimitContext.cacheService();
        int permits = configDto.getPermits();
        String cacheValue = cacheService.get(cacheKey);
        if (StrUtil.isEmpty((CharSequence)cacheKey)) {
            long expireMills = configDto.getTimeUnit().toMillis(configDto.getInterval());
            LogUtils.info((String)"cacheKey: {} \u5bf9\u5e94\u7684\u5386\u53f2\u914d\u7f6e\u4e3a\u7a7a\uff0c\u8fdb\u884c\u521d\u59cb\u5316", (Object[])new Object[0]);
            cacheValue = "0";
            cacheService.set(cacheKey, cacheValue, expireMills);
        }
        if ((newCount = (cacheCount = Long.parseLong(cacheValue)) + (long)permits) > (configCount = configDto.getCount().longValue())) {
            LogUtils.warn((String)"newCount {} \u5927\u4e8e\u914d\u7f6e\u7684 {}", (Object[])new Object[]{newCount, configCount});
            return false;
        }
        long ttlMills = cacheService.ttl(cacheKey);
        if (ttlMills > 0L) {
            cacheService.set(cacheKey, cacheValue, ttlMills);
        }
        return true;
    }
}

