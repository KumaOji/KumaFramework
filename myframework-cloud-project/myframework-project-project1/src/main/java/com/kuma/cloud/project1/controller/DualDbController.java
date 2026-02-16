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

import com.kuma.boot.common.model.request.PageParam;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.project1.model.Item;
import com.kuma.cloud.project1.model.OrderRecord;
import com.kuma.cloud.project1.request.ItemSearchQuery;
import com.kuma.cloud.project1.service.DualDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 双库查询 Controller - 查询 db_a.t_item 与 db_b.t_order
 *
 * @author kuma
 */
@Tag(name = "双库查询", description = "db_a 商品表、db_b 订单表")
@RestController
@RequestMapping("/api/dual-db")
@RequiredArgsConstructor
public class DualDbController {

    private final DualDbService dualDbService;

    @Operation(summary = "查询 db_a 商品列表")
    @GetMapping("/items")
    public Result<List<Item>> listItems() {
        return Result.success(dualDbService.listItems());
    }

    @Operation(summary = "查询 db_b 订单列表")
    @GetMapping("/orders")
    public Result<List<OrderRecord>> listOrders() {
        return Result.success(dualDbService.listOrders());
    }

    @Operation(summary = "分页查询 db_a 商品（PageUtils + selectPage + IPage）")
    @PostMapping("/items/page")
    public Result<PageResult<Item>> pageItems(@Valid @RequestBody PageQuery query) {
        return Result.success(dualDbService.pageItems(query));
    }

    @Operation(summary = "分页查询 db_a 商品（PageParam，GET URL 参数）")
    @GetMapping("/items/page")
    public Result<PageResult<Item>> pageItemsByParam(PageParam pageParam) {
        return Result.success(dualDbService.pageItemsByParam(pageParam));
    }

    @Operation(summary = "分页查询 db_b 订单（PageUtils + selectPage + IPage）")
    @PostMapping("/orders/page")
    public Result<PageResult<OrderRecord>> pageOrders(@Valid @RequestBody PageQuery query) {
        return Result.success(dualDbService.pageOrders(query));
    }

    @Operation(summary = "搜索商品（QueryWrapper: like、ge、le）")
    @PostMapping("/items/search")
    public Result<PageResult<Item>> searchItems(@Valid @RequestBody ItemSearchQuery query) {
        return Result.success(dualDbService.searchItems(query));
    }
}
