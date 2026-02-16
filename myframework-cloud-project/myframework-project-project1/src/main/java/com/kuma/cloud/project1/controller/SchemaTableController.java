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

import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.project1.request.TablePageQuery;
import com.kuma.cloud.project1.service.SchemaTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Schema 表 Controller - 查看 blog_source 库表及分页查询表数据
 *
 * @author kuma
 */
@Tag(name = "Schema 表", description = "blog_source 库表列表及分页查询")
@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
public class SchemaTableController {

    private final SchemaTableService schemaTableService;

    @Operation(summary = "查看 blog_source 库有哪些表")
    @GetMapping("/tables")
    public Result<List<String>> listTables() {
        List<String> tables = schemaTableService.listTables();
        return Result.success(tables);
    }

    @Operation(summary = "分页查看指定表的数据（传入表名）")
    @PostMapping("/table/page")
    public Result<PageResult<Map<String, Object>>> pageTableData(@Valid @RequestBody TablePageQuery query) {
        PageResult<Map<String, Object>> page = schemaTableService.pageTableData(query);
        return Result.success(page);
    }
}
