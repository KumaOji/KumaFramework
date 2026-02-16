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

package com.kuma.cloud.project1.service;

import com.kuma.boot.common.model.request.PageParam;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.cloud.project1.model.Item;
import com.kuma.cloud.project1.model.OrderRecord;
import com.kuma.cloud.project1.request.ItemQueryVO;
import com.kuma.cloud.project1.request.ItemSearchQuery;

import java.util.List;

/**
 * 双库查询服务 - 查询 db_a.t_item 与 db_b.t_order
 *
 * @author kuma
 */
public interface DualDbService {

    /**
     * 查询 db_a 库的商品列表
     */
    List<Item> listItems();

    /**
     * 查询 db_b 库的订单列表
     */
    List<OrderRecord> listOrders();

    /**
     * 分页查询 db_a 商品（使用 PageUtils + selectPage + IPage）
     */
    PageResult<Item> pageItems(PageQuery query);

    /**
     * 分页查询 db_a 商品（PageParam，从 URL 参数解析：?page=1&size=10&sort=id,asc）
     */
    PageResult<Item> pageItemsByParam(PageParam pageParam);

    /**
     * 分页查询 db_a 商品（参考 ArticleController：PageParam + QueryVO）
     */
    PageResult<Item> getItemList(PageParam pageParam, ItemQueryVO queryVO);

    /**
     * 分页查询 db_b 订单（使用 PageUtils + selectPage + IPage）
     */
    PageResult<OrderRecord> pageOrders(PageQuery query);

    /**
     * 分页搜索商品（演示 QueryWrapper 筛选：like、ge、le）
     */
    PageResult<Item> searchItems(ItemSearchQuery query);
}
