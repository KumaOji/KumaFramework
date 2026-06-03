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

import lombok.Value;

/**
 * Relay Connection 规范中的 Edge 节点，包含数据节点和游标.
 *
 * <p>Schema 示例（以 User 为例）：
 * <pre>
 * type UserEdge {
 *   node:   User!
 *   cursor: String!
 * }
 * </pre>
 *
 * @param <T> 业务节点类型
 * @author kuma
 */
@Value
public class Edge<T> {

    T node;
    String cursor;

    public static <T> Edge<T> of(T node, String cursor) {
        return new Edge<>(node, cursor);
    }

    /**
     * 根据列表下标和当前页偏移生成游标.
     *
     * @param node   数据节点
     * @param page   页码（从 1 开始）
     * @param size   每页大小
     * @param index  在当前页中的下标（从 0 开始）
     */
    public static <T> Edge<T> of(T node, int page, int size, int index) {
        long offset = (long) (page - 1) * size + index;
        return new Edge<>(node, "offset:" + offset);
    }
}
