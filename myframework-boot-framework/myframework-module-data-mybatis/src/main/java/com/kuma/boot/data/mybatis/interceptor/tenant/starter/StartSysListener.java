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

package com.kuma.boot.data.mybatis.interceptor.tenant.starter;

import com.alibaba.druid.DbType;
import com.kuma.boot.data.mybatis.interceptor.tenant.MybatisMultiTenantPluginInterceptor;
import com.kuma.boot.data.mybatis.interceptor.tenant.handler.TenantInfoHandler;
import com.kuma.boot.data.mybatis.interceptor.tenant.service.ITenantService;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author wency_cai
 */
public class StartSysListener implements ApplicationListener<ApplicationStartedEvent> {

    private static Logger log = LoggerFactory.getLogger(StartSysListener.class);

    private final List<SqlSessionFactory> sqlSessionFactoryList;

    private final ITenantService tenantService;

    private final com.kuma.boot.data.mybatis.interceptor.tenant.starter.TenantProperties tenantProperties;

    public StartSysListener(
            ITenantService tenantService,
            com.kuma.boot.data.mybatis.interceptor.tenant.starter.TenantProperties tenantProperties,
            List<SqlSessionFactory> sqlSessionFactoryList) {
        this.tenantService = tenantService;
        this.tenantProperties = tenantProperties;
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.debug("添加自定义Mybatis多租户SQL拦截器");
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            // 添加拦截器
            sqlSessionFactory
                    .getConfiguration()
                    .addInterceptor(
                            new MybatisMultiTenantPluginInterceptor(
                                    tenantService,
                                    new TenantInfoHandler() {

                                        @Override
                                        public List getTenantIds() {
                                            return tenantService.getTenantIds();
                                        }

                                        @Override
                                        public List<String> ignoreTableName() {
                                            return tenantProperties.getIgnoreTableName();
                                        }

                                        @Override
                                        public List<String> ignoreMatchTableAlias() {
                                            return tenantProperties.getIgnoreMatchTableAlias();
                                        }

                                        @Override
                                        public List<String> ignoreTableNamePrefix() {
                                            return tenantProperties.getIgnoreTableNamePrefix();
                                        }

                                        @Override
                                        public String getTenantIdColumn() {
                                            return tenantProperties.getTenantIdColumn();
                                        }

                                        @Override
                                        public List<String> ignoreDynamicDatasource() {
                                            return tenantProperties.getIgnoreDynamicDatasource();
                                        }

                                        @Override
                                        public DbType getDbType() {
                                            return tenantProperties.getDbType();
                                        }
                                    }));
        }
    }
}
