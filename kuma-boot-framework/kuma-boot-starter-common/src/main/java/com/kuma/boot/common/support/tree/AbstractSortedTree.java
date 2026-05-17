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

package com.kuma.boot.common.support.tree;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * 抽象可排序树 非线程安全
 *
 * @param <V> 节点值类型
 * @param <I> 节点唯一标识类型
 * @param <N> 当前对象类型
 */
public abstract class AbstractSortedTree<V, I, N extends AbstractSortedTreeNode<V, I, N>>
        extends AbstractTree<V, I, N> {

    public <C extends Collection<N>> AbstractSortedTree(C nodes) {
        super(nodes);
    }

    public <C extends Collection<N>> AbstractSortedTree(C nodes, OrphanPolicy orphanPolicy) {
        super(nodes, orphanPolicy);
    }

    public <C extends Collection<N>> AbstractSortedTree(C nodes, boolean noRoot) {
        super(nodes, noRoot);
    }

    public <C extends Collection<N>> AbstractSortedTree(
            C nodes, boolean noRoot, OrphanPolicy orphanPolicy) {
        super(nodes, noRoot, orphanPolicy);
    }

    @Override
    protected Set<N> initTopNodes() {
        return new TreeSet<>();
    }
}
