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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.dept.config;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.dept.rule.DeptDataPermissionRule;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.dept.rule.DeptDataPermissionRuleCustomizer;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.dept.service.DeptDataPermissionFrameworkService;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/** 系统内置基于部门的数据权限 AutoConfiguration */
@AutoConfiguration
public class DeptDataPermissionAutoConfiguration {

    /**
     * 构建部门数据权限规则对象
     *
     * @param service 数据权限service对象
     * @param customizers 容器中自定义的表规则集合
     * @return 数据权限对象
     */
    @Bean
    public DeptDataPermissionRule deptDataPermissionRule(
            DeptDataPermissionFrameworkService service,
            List<DeptDataPermissionRuleCustomizer> customizers) {
        // 创建数据权限规则对象
        DeptDataPermissionRule rule = new DeptDataPermissionRule(service);
        // 根据配置的自定义规则 补全部门数据权限的表信息
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }
}
