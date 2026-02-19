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

package com.kuma.boot.data.mybatis.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.tx.TransactionalUtils;
import com.kuma.boot.data.datasource.utils.BatchUtils;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisInterceptorProperties;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisProperties;
import com.kuma.boot.data.mybatis.utils.MybatisUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 插件容器
 */
@EnableTransactionManagement
@AutoConfiguration(after = {com.kuma.boot.data.mybatis.autoconfigure.MybatisInterceptorAutoConfiguration.class})
@Import({MybatisUtil.class, TransactionalUtils.class, BatchUtils.class})
@EnableConfigurationProperties({MybatisProperties.class, MybatisInterceptorProperties.class})
@ConditionalOnProperty(
        prefix = MybatisProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class MybatisAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(MybatisAutoConfiguration.class, StarterNameConstants.DATA_MYBATIS_STARTER);
    }
}
