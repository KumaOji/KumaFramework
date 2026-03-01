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

package com.kuma.boot.data.mybatis.mybatisplus.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.kuma.boot.common.model.request.PageQuery;

import java.util.List;

/**
 * MpPage
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class MpPage<T> extends PageDTO<T> {

    public MpPage() {
    }

    public MpPage( long current, long size ) {
        super(current, size);
    }

    public MpPage( long current, long size, long total ) {
        super(current, size, total);
    }

    public MpPage( long current, long size, boolean searchCount ) {
        super(current, size, searchCount);
    }

    public MpPage( long current, long size, long total, boolean searchCount ) {
        super(current, size, total, searchCount);
    }

    public static <T> Page<T> of( long current, long size ) {
        return of(current, size, 0L);
    }

    public static <T> Page<T> of( long current, long size, long total ) {
        return of(current, size, total, true);
    }

    public static <T> Page<T> of( long current, long size, boolean searchCount ) {
        return of(current, size, 0L, searchCount);
    }

    public static <T> Page<T> of( long current, long size, long total, boolean searchCount ) {
        return new MpPage<T>(current, size, total, searchCount);
    }

    public static <T> Page<T> of( PageQuery pageQuery, List<T> records ) {
        Page<T> page = of(pageQuery.getCurrentPage(), pageQuery.getCurrentPage(), records.size());
        page.setRecords(records);
        return page;
    }
}
