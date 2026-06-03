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

package com.kuma.boot.idempotent.idempotetduplicate;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * IdempotentConfiguration
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:13:17
 */
@AutoConfiguration
@ConditionalOnClass(RedissonClient.class)
public class PreventDuplicateSubmitAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(PreventDuplicateSubmitAutoConfiguration.class, StarterNameConstants.IDEMPOTENT_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean
    public PreventDuplicateSubmitAspect preventDuplicateSubmitAspect( RedissonClient redissonClient) {
        return new PreventDuplicateSubmitAspect(redissonClient);
    }
}
