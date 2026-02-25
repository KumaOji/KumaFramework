/*
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.util.ArrayUtil
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.core.annotation.RateLimits;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RateLimitConfigService
implements IRateLimitConfigService {
    @Override
    public List<RateLimitConfigDto> queryConfigList(String tokenId, String methodId, Method method) {
        RateLimits clazzRateLimits;
        RateLimits methodRateLimits;
        RateLimit methodRateLimit = method.getAnnotation(RateLimit.class);
        List<RateLimitConfigDto> methodConfigList = this.buildConfigList(methodRateLimit, methodRateLimits = method.getAnnotation(RateLimits.class));
        if (CollUtil.isNotEmpty(methodConfigList)) {
            return methodConfigList;
        }
        Class<?> clazz = method.getDeclaringClass();
        RateLimit clazzRateLimit = clazz.getAnnotation(RateLimit.class);
        List<RateLimitConfigDto> clazzConfigList = this.buildConfigList(clazzRateLimit, clazzRateLimits = clazz.getAnnotation(RateLimits.class));
        if (CollUtil.isNotEmpty(clazzConfigList)) {
            return clazzConfigList;
        }
        return Collections.emptyList();
    }

    private List<RateLimitConfigDto> buildConfigList(RateLimit rateLimit, RateLimits rateLimits) {
        Object[] rateLimitsArray;
        ArrayList<RateLimitConfigDto> resultList = new ArrayList<RateLimitConfigDto>();
        RateLimitConfigDto rateLimitConfig = this.buildConfig(rateLimit);
        if (rateLimitConfig != null) {
            resultList.add(rateLimitConfig);
        }
        if (rateLimits != null && ArrayUtil.isNotEmpty((Object[])(rateLimitsArray = rateLimits.value()))) {
            for (Object limit : rateLimitsArray) {
                RateLimitConfigDto configDto = this.buildConfig((RateLimit)limit);
                if (configDto == null) continue;
                resultList.add(configDto);
            }
        }
        return resultList;
    }

    private RateLimitConfigDto buildConfig(RateLimit rateLimit) {
        if (rateLimit == null) {
            return null;
        }
        RateLimitConfigDto configDto = new RateLimitConfigDto();
        configDto.setCount(rateLimit.count());
        configDto.setInterval(rateLimit.interval());
        configDto.setPermits(rateLimit.value());
        configDto.setTimeUnit(rateLimit.timeUnit());
        configDto.setEnable(rateLimit.enable());
        return configDto;
    }
}

