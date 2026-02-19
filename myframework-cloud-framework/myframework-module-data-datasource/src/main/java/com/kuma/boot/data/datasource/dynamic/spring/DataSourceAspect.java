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

package com.kuma.boot.data.datasource.dynamic.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * DataSourceAspect
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Aspect
@Component
public class DataSourceAspect {

    @Before("@annotation(com.kuma.boot.data.datasource.dynamic.spring.MasterDataSource)")
    public void setMasterDataSource( JoinPoint joinPoint ) {
        DataSourceContextHolder.setDataSourceType(DataSourceTypeEnum.MASTER);
    }

    @Before("@annotation(com.kuma.boot.data.datasource.dynamic.spring.SlaveDataSource)")
    public void setSlaveDataSource( JoinPoint joinPoint ) {
        DataSourceContextHolder.setDataSourceType(DataSourceTypeEnum.SLAVE);
    }

    @After("@annotation(com.kuma.boot.data.datasource.dynamic.spring.MasterDataSource) || "
            + "@annotation(com.kuma.boot.data.datasource.dynamic.spring.SlaveDataSource)")
    public void clearDataSource( JoinPoint joinPoint ) {
        DataSourceContextHolder.clearDataSourceType();
    }
}
