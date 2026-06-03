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

package com.kuma.boot.graphql.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GraphQL 分页输入参数（偏移分页）.
 *
 * <p>Schema 中声明：
 * <pre>
 * input PageInput {
 *   page: Int!   # 页码，从 1 开始
 *   size: Int!   # 每页条数
 * }
 * </pre>
 *
 * @author kuma
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInput {

    /** 页码（从 1 开始）. */
    @Builder.Default
    private int page = 1;

    /** 每页条数（默认 10，最大 100）. */
    @Builder.Default
    private int size = 10;

    /** 计算 offset. */
    public int offset() {
        return (Math.max(page, 1) - 1) * clampedSize();
    }

    /** 返回经过边界检查的 size. */
    public int clampedSize() {
        return Math.min(Math.max(size, 1), 100);
    }
}
