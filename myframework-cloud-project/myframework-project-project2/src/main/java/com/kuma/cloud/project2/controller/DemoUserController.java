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

package com.kuma.cloud.project2.controller;

import com.kuma.cloud.project2.entity.DemoUser;
import com.kuma.cloud.project2.service.DemoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 演示用户控制器 - data-mybatis 简单使用展示
 *
 * @author kuma
 */
@RestController
@RequestMapping("/demo/user")
@RequiredArgsConstructor
public class DemoUserController {

    private final DemoUserService demoUserService;

    @GetMapping("/list")
    public List<DemoUser> list() {
        return demoUserService.list();
    }

    @GetMapping("/{id}")
    public DemoUser getById(@PathVariable Long id) {
        return demoUserService.getById(id);
    }

    @GetMapping("/name/{name}")
    public DemoUser getByName(@PathVariable String name) {
        return demoUserService.getByName(name);
    }

    @PostMapping
    public int save(@RequestBody DemoUser user) {
        return demoUserService.save(user);
    }
}
