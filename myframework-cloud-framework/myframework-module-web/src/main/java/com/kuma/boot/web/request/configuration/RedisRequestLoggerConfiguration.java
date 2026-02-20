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

package com.kuma.boot.web.request.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.enums.RequestLoggerTypeEnum;
import com.kuma.boot.web.request.properties.RequestLoggerProperties;
import com.kuma.boot.web.request.service.RequestLoggerService;
import com.kuma.boot.web.request.service.impl.RedisRequestLoggerServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:21
 */
@AutoConfiguration
@ConditionalOnClass(RedisRepository.class)
@ConditionalOnBean(RedisRepository.class)
@ConditionalOnProperty(
        prefix = RequestLoggerProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class RedisRequestLoggerConfiguration implements InitializingBean {

    @Autowired private RequestLoggerProperties requestLoggerProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(
                RedisRequestLoggerConfiguration.class, StarterNameConstants.LOGGER_STARTER);
    }

    @Bean
    public RequestLoggerService redisRequestLoggerService(RedisRepository redisRepository) {
        if (Arrays.stream(requestLoggerProperties.getTypes())
                .anyMatch(e -> e.name().equals(RequestLoggerTypeEnum.REDIS.name()))) {
            return new RedisRequestLoggerServiceImpl(redisRepository);
        }
        return null;
    }
}
