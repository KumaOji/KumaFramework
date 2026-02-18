/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.mybatis.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageParam;
import com.kuma.boot.common.model.request.BasePageQuery;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.model.result.PageResultMulti;
import com.kuma.boot.common.model.result.PageResultRecord;

import java.util.Collections;
import java.util.List;

/**
 * 分页工具类，与 common 模块的 PageResult、PageQuery 互转
 *
 * @author kuma
 */
public final class PageUtils {

    private PageUtils() {
    }

    /**
     * PageQuery 转 MyBatis-Plus Page
     */
    public static <T> Page<T> toPage(PageQuery query) {
        if (query == null) {
            return new Page<>(1, 10);
        }
        int current = query.getCurrentPage() != null ? query.getCurrentPage() : 1;
        int size = query.getPageSize() != null ? query.getPageSize() : 10;
        Page<T> page = new Page<>(current, size);
        if (query.getSort() != null && !query.getSort().isBlank()) {
            boolean asc = "asc".equalsIgnoreCase(query.getOrder());
            page.addOrder(asc ? OrderItem.asc(query.getSort()) : OrderItem.desc(query.getSort()));
        }
        return page;
    }

    /**
     * PageParam 转 MyBatis-Plus Page
     */
    public static <T> Page<T> toPage(PageParam pageParam) {
        if (pageParam == null) {
            return new Page<>(1, 10);
        }
        Page<T> page = new Page<>((long) pageParam.getPage(), pageParam.getSize());
        if (pageParam.getSorts() != null && !pageParam.getSorts().isEmpty()) {
            for (PageParam.Sort sort : pageParam.getSorts()) {
                page.addOrder(sort.isAsc() ? OrderItem.asc(sort.getField()) : OrderItem.desc(sort.getField()));
            }
        }
        return page;
    }

    /**
     * BasePageQuery 转 MyBatis-Plus Page
     */
    public static <T> Page<T> toPage(BasePageQuery<?> query) {
        if (query == null) {
            return new Page<>(1, 10);
        }
        int current = query.getCurrentPage() != null ? query.getCurrentPage() : 1;
        int size = query.getPageSize() != null ? query.getPageSize() : 10;
        return new Page<>(current, size);
    }

    /**
     * 当前页、每页条数、排序 转 MyBatis-Plus Page
     */
    public static <T> Page<T> toPage(int currentPage, int pageSize, String sort, String order) {
        Page<T> page = new Page<>(currentPage, pageSize);
        if (sort != null && !sort.isBlank()) {
            boolean asc = "asc".equalsIgnoreCase(order);
            page.addOrder(asc ? OrderItem.asc(sort) : OrderItem.desc(sort));
        }
        return page;
    }

    /**
     * 当前页、每页条数 转 MyBatis-Plus Page
     */
    public static <T> Page<T> toPage(int currentPage, int pageSize) {
        return new Page<>(currentPage, pageSize);
    }

    /**
     * IPage 转 PageResult
     */
    public static <R> PageResult<R> toPageResult(IPage<R> page) {
        if (page == null) {
            return PageResult.of(0, 0, 1, 10, Collections.emptyList());
        }
        return PageResult.of(
                page.getTotal(),
                (int) page.getPages(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                page.getRecords()
        );
    }

    /**
     * IPage 转 PageResultMulti
     */
    public static <R> PageResultMulti<R> toPageResultMulti(IPage<R> page) {
        if (page == null) {
            return PageResultMulti.of(0, 0, 1, 10, Collections.emptyList());
        }
        return PageResultMulti.of(
                page.getTotal(),
                (int) page.getPages(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                page.getRecords()
        );
    }

    /**
     * IPage 转 PageResultRecord
     */
    public static <R> PageResultRecord<R> toPageResultRecord(IPage<R> page) {
        if (page == null) {
            return PageResultRecord.of(0, 0, 1, 10, Collections.emptyList());
        }
        return PageResultRecord.of(
                page.getTotal(),
                (int) page.getPages(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                page.getRecords()
        );
    }

    /**
     * 从 IPage 提取数据列表
     */
    public static <R> List<R> toList(IPage<R> page) {
        return page != null ? page.getRecords() : Collections.emptyList();
    }
}
