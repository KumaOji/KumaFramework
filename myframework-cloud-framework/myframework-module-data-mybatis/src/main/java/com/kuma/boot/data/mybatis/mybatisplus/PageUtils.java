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

package com.kuma.boot.data.mybatis.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;

/**
 * PageUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class PageUtils {

    public static <T, R> IPage<R> convertPage( IPage<T> iPage, Function<T, R> function ) {
        List<R> records = iPage.getRecords().stream().map(function).toList();

        IPage<R> result = new Page<>();
        result.setPages(iPage.getPages());
        result.setCurrent(iPage.getCurrent());
        result.setSize(iPage.getSize());
        result.setCurrent(iPage.getCurrent());
        result.setRecords(records);
        return result;
    }

    public static <T, R> List<R> convertRecords( IPage<T> iPage, Function<T, R> function ) {
        return iPage.getRecords().stream().map(function).toList();
    }
}
