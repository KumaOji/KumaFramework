/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.DataVersionLogService;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.impl.DataVersionLogLoggerService;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusInterceptorProperties;
import java.util.Arrays;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:21
 */
@AutoConfiguration
@ConditionalOnProperty(
        prefix = MybatisPlusInterceptorProperties.DATA_CHANGE_PREFIX,
        name = "enabled",
        havingValue = "true")
public class DataVersionLogLoggerServiceConfiguration implements InitializingBean {

    @Autowired private MybatisPlusInterceptorProperties mybatisPlusInterceptorProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.configuration.DataVersionLogKafkaServiceConfiguration.class, StarterNameConstants.LOGGER_STARTER);
    }

    @Bean
    public DataVersionLogService dataVersionLogLoggerService() {
        MybatisPlusInterceptorProperties.DataChange.StoreType[] types =
                mybatisPlusInterceptorProperties.getDataChange().getTypes();
        if (Arrays.stream(types)
                .anyMatch(
                        e ->
                                e.name()
                                        .equals(
                                                MybatisPlusInterceptorProperties.DataChange
                                                        .StoreType.LOGGER
                                                        .name()))) {
            return new DataVersionLogLoggerService();
        }
        return null;
    }
}
