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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.scope;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.code.DataScopeEnum;
import java.util.Set;

/** 数据权限范围参数 */
public class DataPermScope {

    /** 范围类型 自身,部门,人员,部门和人员 */
    private DataScopeEnum scopeType;

    /** 对应部门ID集合 */
    private Set<Long> deptScopeIds;

    /** 对应用户ID集合 */
    private Set<Long> userScopeIds;

    public DataScopeEnum getScopeType() {
        return scopeType;
    }

    public void setScopeType(DataScopeEnum scopeType) {
        this.scopeType = scopeType;
    }

    public Set<Long> getDeptScopeIds() {
        return deptScopeIds;
    }

    public void setDeptScopeIds(Set<Long> deptScopeIds) {
        this.deptScopeIds = deptScopeIds;
    }

    public Set<Long> getUserScopeIds() {
        return userScopeIds;
    }

    public void setUserScopeIds(Set<Long> userScopeIds) {
        userScopeIds = userScopeIds;
    }
}
