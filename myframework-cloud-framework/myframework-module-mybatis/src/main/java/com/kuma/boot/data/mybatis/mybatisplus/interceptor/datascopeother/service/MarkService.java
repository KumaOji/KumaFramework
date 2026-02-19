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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.service;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.DataScopeInfo;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.MarkDto;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.MarkQuery;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.PageData;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.Rule;
import java.util.List;

public interface MarkService {

    Rule getByName(String name);

    DataScopeInfo execRuleByName(String name);

    DataScopeInfo execRuleByEntity(Rule entity);

    boolean existSameName(Long id, String scopeName);

    PageData<MarkDto> page(MarkQuery query);

    void enabledSwitch(Long id);

    List<MarkDto> list(MarkQuery query);

    void removeAllByRoleIdAndMarkId(Long roleId, Long markId);

    boolean addRelation(Long roleId, Long markId, Long ruleId);
}
