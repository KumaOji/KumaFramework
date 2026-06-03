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

import java.util.List;
import java.util.function.Function;
import lombok.Value;

/**
 * Relay Connection 规范分页响应，包含 edges 列表和分页信息.
 *
 * <p>Schema 示例（以 User 为例）：
 * <pre>
 * type UserConnection {
 *   edges:    [UserEdge!]!
 *   pageInfo: PageInfo!
 * }
 * </pre>
 *
 * <p>典型用法：
 * <pre>
 * long total = userRepository.count();
 * List&lt;User&gt; users = userRepository.findAll(page.offset(), page.clampedSize());
 * return Connection.of(users, page.getPage(), page.clampedSize(), total, Function.identity());
 * </pre>
 *
 * @param <T> 业务节点类型
 * @author kuma
 */
@Value
public class Connection<T> {

    List<Edge<T>> edges;
    PageInfo pageInfo;

    /**
     * 从列表和分页信息构建 Connection.
     *
     * @param items      当前页数据
     * @param page       页码（从 1 开始）
     * @param size       每页大小
     * @param totalCount 总记录数
     * @param mapper     原始类型 → Edge 节点类型的转换函数（传 {@code Function.identity()} 不做转换）
     */
    public static <S, T> Connection<T> of(List<S> items, int page, int size,
                                          long totalCount, Function<S, T> mapper) {
        List<Edge<T>> edges = new java.util.ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            edges.add(Edge.of(mapper.apply(items.get(i)), page, size, i));
        }
        return new Connection<>(edges, PageInfo.of(page, size, totalCount));
    }

    /** 空 Connection，用于无数据场景. */
    public static <T> Connection<T> empty() {
        return new Connection<>(List.of(), PageInfo.builder()
                .hasNextPage(false).hasPreviousPage(false).totalCount(0).build());
    }

    /** 获取所有节点（去掉 cursor 包装），方便业务层直接使用. */
    public List<T> nodes() {
        return edges.stream().map(Edge::getNode).toList();
    }
}
