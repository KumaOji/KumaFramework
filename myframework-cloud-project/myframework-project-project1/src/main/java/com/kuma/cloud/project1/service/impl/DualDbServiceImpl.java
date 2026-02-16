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

package com.kuma.cloud.project1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageParam;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.datasource.context.SchemaContext;
import com.kuma.boot.mybatis.page.PageUtils;
import com.kuma.cloud.project1.mapper.ItemMapper;
import com.kuma.cloud.project1.mapper.OrderRecordMapper;
import com.kuma.cloud.project1.model.Item;
import com.kuma.cloud.project1.model.OrderRecord;
import com.kuma.cloud.project1.request.ItemQueryVO;
import com.kuma.cloud.project1.request.ItemSearchQuery;
import com.kuma.cloud.project1.service.DualDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 双库查询服务实现 - 通过 Schema 切换查询 db_a、db_b
 *
 * @author kuma
 */
@Service
@RequiredArgsConstructor
public class DualDbServiceImpl implements DualDbService {

    private static final String SCHEMA_DB_A = "db_a";
    private static final String SCHEMA_DB_B = "db_b";

    private final ItemMapper itemMapper;
    private final OrderRecordMapper orderRecordMapper;

    @Override
    public List<Item> listItems() {
        return SchemaContext.withSchema(SCHEMA_DB_A,
                () -> itemMapper.selectList(Wrappers.lambdaQuery(Item.class).orderByAsc(Item::getId)));
    }

    @Override
    public List<OrderRecord> listOrders() {
        return SchemaContext.withSchema(SCHEMA_DB_B,
                () -> orderRecordMapper.selectList(Wrappers.lambdaQuery(OrderRecord.class).orderByAsc(OrderRecord::getId)));
    }

    @Override
    public PageResult<Item> pageItems(PageQuery query) {
        Page<Item> page = PageUtils.toPage(query);
        IPage<Item> result = SchemaContext.withSchema(SCHEMA_DB_A,
                () -> itemMapper.selectPage(page, Wrappers.lambdaQuery(Item.class)));
        return PageUtils.toPageResult(result);
    }

    @Override
    public PageResult<Item> pageItemsByParam(PageParam pageParam) {
        Page<Item> page = PageUtils.toPage(pageParam);
        IPage<Item> result = SchemaContext.withSchema(SCHEMA_DB_A,
                () -> itemMapper.selectPage(page, Wrappers.lambdaQuery(Item.class)));
        return PageUtils.toPageResult(result);
    }

    @Override
    public PageResult<Item> getItemList(PageParam pageParam, ItemQueryVO queryVO) {
        Page<Item> page = PageUtils.toPage(pageParam);
        var wrapper = Wrappers.<Item>lambdaQuery()
                .like(queryVO != null && StringUtils.hasText(queryVO.getName()), Item::getName, queryVO != null ? queryVO.getName() : null)
                .ge(queryVO != null && queryVO.getMinPrice() != null, Item::getPrice, queryVO != null ? queryVO.getMinPrice() : null)
                .le(queryVO != null && queryVO.getMaxPrice() != null, Item::getPrice, queryVO != null ? queryVO.getMaxPrice() : null);
        IPage<Item> result = SchemaContext.withSchema(SCHEMA_DB_A,
                () -> itemMapper.selectPage(page, wrapper));
        return PageUtils.toPageResult(result);
    }

    @Override
    public PageResult<OrderRecord> pageOrders(PageQuery query) {
        QueryWrapper<OrderRecord> objectQueryWrapper = new QueryWrapper<>();
        Page<OrderRecord> page = PageUtils.toPage(query);

        IPage<OrderRecord> result = SchemaContext.withSchema(SCHEMA_DB_B,
                () -> orderRecordMapper.selectPage(page, objectQueryWrapper));
        return PageUtils.toPageResult(result);
    }

    @Override
    public PageResult<Item> searchItems(ItemSearchQuery query) {
        Page<Item> page = PageUtils.toPage(query);
        LambdaQueryWrapper<Item> wrapper = Wrappers.<Item>lambdaQuery()
                .like(StringUtils.hasText(query.getName()), Item::getName, query.getName())   // name LIKE '%x%'
                .ge(query.getMinPrice() != null, Item::getPrice, query.getMinPrice())         // price >= minPrice
                .le(query.getMaxPrice() != null, Item::getPrice, query.getMaxPrice());// price <= maxPrice
        IPage<Item> result = SchemaContext.withSchema(SCHEMA_DB_A,
                () -> itemMapper.selectPage(page, wrapper));
        return PageUtils.toPageResult(result);
    }
}
