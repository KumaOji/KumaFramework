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

package com.kuma.boot.data.mybatis.interceptor.page;

import org.apache.ibatis.session.RowBounds;

/**
 * PageRowBounds
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class PageRowBounds extends RowBounds {

    private Long total;

    public PageRowBounds( int offset, int limit ) {
        super(offset, limit);
    }

    public PageRowBounds() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal( Long total ) {
        this.total = total;
    }
}
