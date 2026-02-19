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

package com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 查询参数 */
@Schema(title = "查询参数")
public class QueryParams {

    @Schema(description = "参数集合")
    private List<QueryParam> queryParams;

    @Schema(description = "排序集合")
    private List<QueryOrder> queryOrders;

    public List<QueryParam> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<QueryParam> queryParams) {
        this.queryParams = queryParams;
    }

    public List<QueryOrder> getQueryOrders() {
        return queryOrders;
    }

    public void setQueryOrders(List<QueryOrder> queryOrders) {
        this.queryOrders = queryOrders;
    }
}
