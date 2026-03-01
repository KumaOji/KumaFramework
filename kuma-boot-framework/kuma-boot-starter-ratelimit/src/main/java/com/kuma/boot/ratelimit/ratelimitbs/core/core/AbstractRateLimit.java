/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListenerContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.reject.RateLimitRejectListenerContext;
import com.kuma.boot.ratelimit.ratelimitbs.core.util.InnerRateLimitUtils;
import cn.hutool.core.collection.CollUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 适配器
 */
public abstract class AbstractRateLimit implements IRateLimit {

    /**
     * 执行
     *
     * @param cacheKey         缓存标识
     * @param configDto        配置
     * @param rateLimitContext 上下文
     * @return 结果
     */
    protected abstract boolean doAcquire(
            String cacheKey, RateLimitConfigDto configDto, IRateLimitContext rateLimitContext);

    @Override
    public boolean tryAcquire(IRateLimitContext context) {
        // 1. 基本信息
        final Method method = context.method();
        final Object[] args = context.args();
        final IRateLimitTokenService tokenService = context.tokenService();
        final IRateLimitMethodService methodService = context.methodService();
        final String tokenId = tokenService.getTokenId(args);
        final String methodId = methodService.getMethodId(method, args);
        final String cacheKeyNamespace = context.cacheKeyNamespace();

        // 2. 查询配置信息
        final IRateLimitConfigService configService = context.configService();
        List<RateLimitConfigDto> configDtoList = configService.queryConfigList(tokenId, methodId, method);

        // 2.1 只保留启用的配置
        List<RateLimitConfigDto> enableConfigList =
                CollectionUtils.conditionList(configDtoList, RateLimitConfigDto::isEnable);

        // 3. 最后的结果
        boolean acquireFlag;
        if (CollUtil.isEmpty(enableConfigList)) {
            LogUtils.info("method {} 对应的配置为空，不做限制", methodId);
            acquireFlag = true;
        } else {
            acquireFlag = tryAcquire(enableConfigList, methodId, tokenId, context);
        }

        final IRateLimitRejectListener rejectListener = context.rejectListener();
        final IRateLimitRejectListenerContext rejectListenerContext = RateLimitRejectListenerContext.newInstance()
                .acquireFlag(acquireFlag)
                .method(method)
                .args(args)
                .rejectListener(rejectListener)
                .tokenService(tokenService)
                .methodService(methodService)
                .configService(configService)
                .cacheService(context.cacheService())
                .configList(enableConfigList)
                .timer(context.timer())
                .cacheKeyNamespace(cacheKeyNamespace);

        rejectListener.listen(rejectListenerContext);

        return acquireFlag;
    }

    protected boolean tryAcquire(
            List<RateLimitConfigDto> configDtoList, String methodId, String tokenId, final IRateLimitContext context) {
        // 全部通过则为通过
        final Set<Long> rateSet = new HashSet<>();
        List<Boolean> resultFlagList = new ArrayList<>();

        // 需要全部遍历，不然对应的消耗数据不准
        final String cacheKeyNamespace = context.cacheKeyNamespace();

        for (RateLimitConfigDto configDto : configDtoList) {
            // 速率
            Long rate = InnerRateLimitUtils.calcRate(configDto);

            // 主要是避免令牌被重复消费的问题
            if (rateSet.contains(rate)) {
                LogUtils.info("配置 {} 对应的速率已存在 {}", configDto, rate);
                continue;
            }
            rateSet.add(rate);

            // 构建 key
            String cacheKey = buildCacheKey(cacheKeyNamespace, tokenId, methodId, rate);
            // 执行结果
            boolean resultFlag = doAcquire(cacheKey, configDto, context);

            resultFlagList.add(resultFlag);
        }

        // 全部通过，才认为是通过
        return !resultFlagList.contains(Boolean.FALSE);
    }

    /**
     * 构建缓存对应的 key
     * <p>
     * 如果这里不暴露给用户，会导致不同的应用的 redis key 重复。
     * 1. 直接暴露一个命名空间会比较好。
     *
     * @param cacheKeyNamespace 命名空间
     * @param tokenId           token 标识
     * @param methodId          方法标识
     * @param rate              速率
     * @return 结果
     */
    protected String buildCacheKey(String cacheKeyNamespace, String tokenId, String methodId, Long rate) {
        String format = "%s:%s:%s:%s";
        return String.format(format, cacheKeyNamespace, tokenId, methodId, rate);
    }
}
