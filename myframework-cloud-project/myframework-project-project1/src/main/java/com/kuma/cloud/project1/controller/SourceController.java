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

package com.kuma.cloud.project1.controller;

import com.kuma.boot.common.model.domain.PageParam;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.project1.entity.Source;
import com.kuma.cloud.project1.service.SourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资源 Controller - 测试分页查询 blog_source.source 表
 *
 * @author kuma
 */
@Tag(name = "资源管理", description = "blog_source 库 source 表")
@RestController
@RequestMapping("/api/sources")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;

    @Operation(summary = "分页查询资源列表")
    @GetMapping
    public Result<PageResult<Source>> pageList(
            PageParam pageParam,
            @Parameter(description = "资源名称（模糊查询）") @RequestParam(required = false) String name) {
        PageResult<Source> page = sourceService.pageList(pageParam, name);
        return Result.success(page);
    }

    @Operation(summary = "根据ID查询资源")
    @GetMapping("/{id}")
    public Result<Source> getById(@PathVariable Long id) {
        Source source = sourceService.getById(id);
        return Result.success(source);
    }
}
