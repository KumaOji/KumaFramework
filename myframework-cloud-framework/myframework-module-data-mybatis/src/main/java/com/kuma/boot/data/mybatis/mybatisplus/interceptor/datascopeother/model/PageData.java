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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * PageData
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class PageData<T> {

    private List<T> list;

    private Long total;

    private Long pages;

    public PageData( IPage<T> page ) {
        this.list = page.getRecords();
        this.pages = page.getPages();
        this.total = page.getTotal();
    }

    public List<T> getList() {
        return list;
    }

    public void setList( List<T> list ) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal( Long total ) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages( Long pages ) {
        this.pages = pages;
    }
}
