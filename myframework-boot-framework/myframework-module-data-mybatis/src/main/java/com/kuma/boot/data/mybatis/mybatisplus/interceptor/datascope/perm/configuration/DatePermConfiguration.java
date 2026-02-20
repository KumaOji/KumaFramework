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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.configuration;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.MpInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.scope.DataScopeInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.select.SelectFieldPermInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 数据权限配置 */
@Configuration
public class DatePermConfiguration {

    /** 数据范围权限插件 */
    @Bean
    @ConditionalOnBean(DataScopeInterceptor.class)
    @ConditionalOnProperty(
            prefix = "bootx.starter.data-perm",
            value = "enableDataPerm",
            havingValue = "true",
            matchIfMissing = true)
    public MpInterceptor dataPermInterceptorMp(DataScopeInterceptor dataScopeInterceptor) {
        return new MpInterceptor(dataScopeInterceptor);
    }

    /** 查询字段权限插件 */
    @Bean
    @ConditionalOnProperty(
            prefix = "bootx.starter.data-perm",
            value = "enableSelectFieldPerm",
            havingValue = "true",
            matchIfMissing = true)
    public MpInterceptor selectFieldPermInterceptorMp(
            SelectFieldPermInterceptor bootxDataPermissionHandler) {
        return new MpInterceptor(bootxDataPermissionHandler);
    }
}
