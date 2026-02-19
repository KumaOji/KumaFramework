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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.configuration;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.listener.DataVersionLogListener;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.DataVersionLogService;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusInterceptorProperties;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:21
 */
@AutoConfiguration(
        after = {
                com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.configuration.DataVersionLogRedisServiceConfiguration.class,
                com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.configuration.DataVersionLogKafkaServiceConfiguration.class,
                com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.configuration.DataVersionLogLoggerServiceConfiguration.class
        })
@ConditionalOnProperty(
        prefix = MybatisPlusInterceptorProperties.DATA_CHANGE_PREFIX,
        name = "enabled",
        havingValue = "true")
public class DataVersionLogConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DataVersionLogConfiguration.class, StarterNameConstants.LOGGER_STARTER);
    }

    @Bean
    public DataVersionLogListener dataVersionLogListener(
            List<DataVersionLogService> requestLoggerServices) {
        return new DataVersionLogListener(requestLoggerServices);
    }
}
