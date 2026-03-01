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

package com.kuma.boot.security.spring.access.vip;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import java.util.HashSet;
import java.util.Set;

public class VipSecurityOauthService {

    private final RedisRepository redisRepository;

    public VipSecurityOauthService(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    /** 动态加载权限-角色信息 */
//    public Set<PermRoleEntity> loadPerms() {
//        Set<PermRoleEntity> permRoleEntitySet = new HashSet<>();
//        // permRoleEntitySet.add(
//        //	new PermRoleEntity().setAccessUri("/demo/admin")
//        //		.setConfigAttributeList(
//        //		SecurityConfig.createList("admin")));
//        // permRoleEntitySet.add(new PermRoleEntity().setAccessUri("/auth/**")
//        //	.setConfigAttributeList(SecurityConfig.createList("admin")));
//        // permRoleEntitySet.add(new PermRoleEntity().setAccessUri("/demo/sp-admin")
//        //	.setConfigAttributeList(SecurityConfig.createList("sp_admin")));
//        return permRoleEntitySet;
//    }
}
