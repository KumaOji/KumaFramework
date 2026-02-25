/*
 *  cn.hutool.core.collection.CollUtil
 *  com.kuma.boot.common.utils.collection.CollectionUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.reject.RateLimitRejectListenerContext;
import com.kuma.boot.ratelimit.ratelimitbs.core.util.InnerRateLimitUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractRateLimit
implements IRateLimit {
    protected abstract boolean doAcquire(String var1, RateLimitConfigDto var2, IRateLimitContext var3);

    @Override
    public boolean tryAcquire(IRateLimitContext context) {
        boolean acquireFlag;
        Method method = context.method();
        Object[] args = context.args();
        IRateLimitTokenService tokenService = context.tokenService();
        IRateLimitMethodService methodService = context.methodService();
        String tokenId = tokenService.getTokenId(args);
        String methodId = methodService.getMethodId(method, args);
        String cacheKeyNamespace = context.cacheKeyNamespace();
        IRateLimitConfigService configService = context.configService();
        List<RateLimitConfigDto> configDtoList = configService.queryConfigList(tokenId, methodId, method);
        List enableConfigList = CollectionUtils.conditionList(configDtoList, RateLimitConfigDto::isEnable);
        if (CollUtil.isEmpty((Collection)enableConfigList)) {
            LogUtils.info((String)"method {} \u5bf9\u5e94\u7684\u914d\u7f6e\u4e3a\u7a7a\uff0c\u4e0d\u505a\u9650\u5236", (Object[])new Object[]{methodId});
            acquireFlag = true;
        } else {
            acquireFlag = this.tryAcquire(enableConfigList, methodId, tokenId, context);
        }
        IRateLimitRejectListener rejectListener = context.rejectListener();
        RateLimitRejectListenerContext rejectListenerContext = RateLimitRejectListenerContext.newInstance().acquireFlag(acquireFlag).method(method).args(args).rejectListener(rejectListener).tokenService(tokenService).methodService(methodService).configService(configService).cacheService(context.cacheService()).configList(enableConfigList).timer(context.timer()).cacheKeyNamespace(cacheKeyNamespace);
        rejectListener.listen(rejectListenerContext);
        return acquireFlag;
    }

    protected boolean tryAcquire(List<RateLimitConfigDto> configDtoList, String methodId, String tokenId, IRateLimitContext context) {
        HashSet<Long> rateSet = new HashSet<Long>();
        ArrayList<Boolean> resultFlagList = new ArrayList<Boolean>();
        String cacheKeyNamespace = context.cacheKeyNamespace();
        for (RateLimitConfigDto configDto : configDtoList) {
            Long rate = InnerRateLimitUtils.calcRate(configDto);
            if (rateSet.contains(rate)) {
                LogUtils.info((String)"\u914d\u7f6e {} \u5bf9\u5e94\u7684\u901f\u7387\u5df2\u5b58\u5728 {}", (Object[])new Object[]{configDto, rate});
                continue;
            }
            rateSet.add(rate);
            String cacheKey = this.buildCacheKey(cacheKeyNamespace, tokenId, methodId, rate);
            boolean resultFlag = this.doAcquire(cacheKey, configDto, context);
            resultFlagList.add(resultFlag);
        }
        return !resultFlagList.contains(Boolean.FALSE);
    }

    protected String buildCacheKey(String cacheKeyNamespace, String tokenId, String methodId, Long rate) {
        String format = "%s:%s:%s:%s";
        return String.format(format, cacheKeyNamespace, tokenId, methodId, rate);
    }
}

