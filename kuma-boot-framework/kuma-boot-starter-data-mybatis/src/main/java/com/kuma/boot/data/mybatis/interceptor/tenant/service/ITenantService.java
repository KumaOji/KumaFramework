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

package com.kuma.boot.data.mybatis.interceptor.tenant.service;

import java.util.List;

/**
 * 提供外部实现接口以获取系统多租户ID服务接口
 *
 * @author wency_cai
 */
public interface ITenantService<T> {

    /**
     * 多租户ID可有一个或多个, 数据类型：Number或String
     *
     * @return 返回租户ID集合
     */
    List<T> getTenantIds();

    /**
     * 多数据源-获取外部当前执行的数据源，默认所有数据源都需要设置租户ID
     *
     * @return 返回当前执行的数据源，不为空则根据配置忽略的数据源跳过设置租户ID
     */
    default String ignoreDynamicDatasource() {
        return null;
    }
}
