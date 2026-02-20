/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use it except in compliance with the License.
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

package com.kuma.cloud.project2.service;

import com.kuma.cloud.project2.entity.DemoUser;
import com.kuma.cloud.project2.mapper.DemoUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 演示用户服务 - data-mybatis 简单使用展示
 *
 * @author kuma
 */
@Service
@RequiredArgsConstructor
public class DemoUserService {

    private final DemoUserMapper demoUserMapper;

    public List<DemoUser> list() {
        return demoUserMapper.selectList();
    }

    public DemoUser getById(Long id) {
        return demoUserMapper.selectById(id);
    }

    public DemoUser getByName(String name) {
        return demoUserMapper.selectOne("name", name);
    }

    public int save(DemoUser user) {
        return demoUserMapper.insert(user);
    }
}
