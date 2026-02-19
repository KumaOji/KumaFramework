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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.factory;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.annotation.DataPermission;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.aop.DataPermissionContextHolder;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.rule.DataPermissionRule;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;

/** 默认的数据权限规则工厂实现类 支持通过DataPermissionContextHolder过滤数据权限 获取生效的数据权限规则 */
public class DataPermissionRuleFactoryImpl implements com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.factory.DataPermissionRuleFactory {

    /** 容器中的数据权限规则数组 */
    private final List<DataPermissionRule> rules;

    public DataPermissionRuleFactoryImpl(List<DataPermissionRule> rules) {
        this.rules = rules;
    }

    /**
     * 获取生效的数据权限规则
     *
     * @return 生效的数据权限规则数组
     */
    @Override
    public List<DataPermissionRule> getDataPermissionRule() {
        // 通过上下文获取数据权限注解
        DataPermission dataPermission = DataPermissionContextHolder.get();

        // 1. 容器中没有数据权限规则的配置
        if (CollUtil.isEmpty(rules)) {
            // 返回空
            return Collections.emptyList();
        }

        // 2. 没有配置数据权限信息 则默认开启 生效所有规则
        if (Objects.isNull(dataPermission)) {
            // 返回容器中配置的所有数据权限规则
            return rules;
        }

        // 3. 已配置数据权限信息 但是禁用 返回空
        if (!dataPermission.enable()) {
            // 返回空
            return Collections.emptyList();
        }

        // 4. 已配置数据权限信息 但是仅生效部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.includeRules())) {
            // 过滤出生效的数据权限规则
            return rules.stream()
                    .filter(
                            rule ->
                                    ArrayUtil.contains(
                                            dataPermission.includeRules(), rule.getClass()))
                    .toList();
        }

        // 5. 已配置数据权限 但是排除部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.excludeRules())) {
            // 过滤出需要排除的数据权限规则
            return rules.stream()
                    .filter(
                            rule ->
                                    !ArrayUtil.contains(
                                            dataPermission.excludeRules(), rule.getClass()))
                    .toList();
        }

        // 6. 已配置数据权限信息 全部规则生效
        return rules;
    }
}
