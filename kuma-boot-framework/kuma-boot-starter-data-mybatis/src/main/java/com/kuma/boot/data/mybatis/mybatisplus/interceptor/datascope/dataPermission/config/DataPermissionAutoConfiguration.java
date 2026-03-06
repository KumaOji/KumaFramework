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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.config;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.MpInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.aop.DataPermissionAnnotationAdvisor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.db.DataPermissionDatabaseInterceptor;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.factory.DataPermissionRuleFactory;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.factory.DataPermissionRuleFactoryImpl;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.rule.DataPermissionRule;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/** 数据权限的自动配置类 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class DataPermissionAutoConfiguration {

    /**
     * 数据权限规则工厂 管理数据权限规则
     *
     * @param rules 容器中的数据权限规则类
     */
    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DataPermissionRuleFactoryImpl(rules);
    }

    /**
     * 数据权限拦截器。通过 MpInterceptor 注册，由项目的 mybatisPlusInterceptor 收集，
     * 避免与 defaultMybatisPlusInterceptor 的循环依赖。sortNo=194 确保在分页插件(199)之前执行。
     */
    @Bean
    public MpInterceptor dataPermissionDatabaseInterceptor(List<DataPermissionRule> rules) {
        DataPermissionRuleFactory ruleFactory = dataPermissionRuleFactory(rules);
        DataPermissionDatabaseInterceptor inner = new DataPermissionDatabaseInterceptor(ruleFactory);
        return new MpInterceptor(inner, 194);
    }

    /** aop处理 */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }
}
