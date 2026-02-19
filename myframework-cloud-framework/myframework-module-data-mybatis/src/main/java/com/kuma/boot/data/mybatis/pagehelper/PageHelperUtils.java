/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.pagehelper;

import com.github.pagehelper.PageInfo;
import com.kuma.boot.common.utils.lang.StringUtils;

import java.io.Serializable;

/**
 * PageHelperUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class PageHelperUtils implements Serializable {

    private static final long serialVersionUID = 377943433889798799L;

    public static <T> com.kuma.boot.data.mybatis.pagehelper.PagedList<T> exportPagedList(com.kuma.boot.data.mybatis.pagehelper.PageParam<T> pageParam ) {
        com.kuma.boot.data.mybatis.pagehelper.PagedList<T> pl = new com.kuma.boot.data.mybatis.pagehelper.PagedList<T>();
        // pagesize
        int pageSize = pageParam.getPageSize();
        if (pageSize <= 0) {
            pageSize = 10;
        } else {
            pl.setPageSize(pageSize);
        }
        int pageNum = pageParam.getPageNum();
        pl.setPageNum(pageNum);
        String orderBy = pageParam.getOrderBy();
        if (StringUtils.isNotEmpty(orderBy)) {
            // 防止sql注入
            String orderBySql = com.kuma.boot.data.mybatis.pagehelper.SQLFilter.sqlInject(orderBy);
            pl.setOrderBy(orderBySql);
        }
        return pl;
    }

    public static <T> com.kuma.boot.data.mybatis.pagehelper.PagedList<T> toPageList(PageInfo<T> spage ) {
        com.kuma.boot.data.mybatis.pagehelper.PagedList<T> pagedList = new com.kuma.boot.data.mybatis.pagehelper.PagedList<T>();
        pagedList.setPageSize((int) spage.getPageSize());
        pagedList.setPageNum((int) spage.getPageNum());
        pagedList.setRecordCount((int) spage.getTotal());
        pagedList.setData(spage.getList());
        pagedList.setPageCount((int) spage.getPages());
        return pagedList;
    }
}
