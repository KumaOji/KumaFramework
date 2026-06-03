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

import lombok.Builder;
import lombok.Value;

/**
 * Relay Connection 规范中的 {@code PageInfo} 对象.
 *
 * <p>Schema 中声明：
 * <pre>
 * type PageInfo {
 *   hasNextPage:     Boolean!
 *   hasPreviousPage: Boolean!
 *   startCursor:     String
 *   endCursor:       String
 *   totalCount:      Int
 * }
 * </pre>
 *
 * @author kuma
 */
@Value
@Builder
public class PageInfo {

    boolean hasNextPage;
    boolean hasPreviousPage;
    String startCursor;
    String endCursor;
    long totalCount;

    /**
     * 根据偏移分页参数快速构建 PageInfo.
     *
     * @param page       当前页码（从 1 开始）
     * @param size       每页大小
     * @param totalCount 总记录数
     */
    public static PageInfo of(int page, int size, long totalCount) {
        long totalPages = (totalCount + size - 1) / size;
        return PageInfo.builder()
                .hasNextPage(page < totalPages)
                .hasPreviousPage(page > 1)
                .startCursor(encodeCursor(page, size, 0))
                .endCursor(encodeCursor(page, size, size - 1))
                .totalCount(totalCount)
                .build();
    }

    private static String encodeCursor(int page, int size, int offsetInPage) {
        long offset = (long) (page - 1) * size + offsetInPage;
        return "offset:" + offset;
    }
}
