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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.dept.service;

import com.kuma.boot.common.model.BaseSecurityUser;

/** 基于部门的数据权限 Framework Service */
public class DeptDataPermissionFrameworkService {

    // private final RemoteRoleService remoteRoleService;

    /**
     * 获得登陆用户的部门数据权限
     *
     * @param loginUser 登陆用户
     * @return 部门数据权限
     */
    public com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.dept.service.DeptDataPermissionRespEntity getDeptDataPermission(BaseSecurityUser loginUser) {
        // log.info("查询用户:{}的数据权限", loginUser);
        // DeptDataPermissionRespDTO respDTO = remoteRoleService.getDataPermission().getData();
        // log.info("数据权限是:{}", respDTO);
        return null;
    }
}
