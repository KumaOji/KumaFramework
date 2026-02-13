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
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * 抽象树节点 用于构造树
 *
 * @param <V> 节点值类型
 * @param <I> 节点唯一标识类型
 * @param <N> 当前对象类型
 */
public abstract class AbstractTreeNode<V, I, N extends AbstractTreeNode<V, I, N>> {

    /**
     * 节点的值对象
     */
    protected final V value;

    /**
     * 层级 最顶层是1 下面的节点依次+1
     */
    private Integer layer;

    /**
     * 父节点
     */
    protected N parent;

    /**
     * 所有子节点 要保证不重复 所以用Set
     */
    protected Set<N> children;

    /**
     * 构造节点
     * @param value 节点值
     */
    public AbstractTreeNode(V value) {
        this.value = value;
    }

    /**
     * 添加子节点
     * @param node 子节点
     */
    protected void addChild(N node) {
        if (children == null) {
            children = initChildren();
        }
        children.add(node);
        @SuppressWarnings("unchecked")
        N parent = (N) this;
        node.parent = parent;
    }

    /**
     * 隔离节点 去掉关系
     */
    protected void isolate() {
        if (parent != null && parent.children != null) {
            parent.children.remove(this);
            parent = null;
        }
        if (children != null) {
            children.forEach(child -> child.parent = null);
            children.clear();
        }
    }

    /**
     * 获取层数 需要树化后才能调用
     * @return 层数 无效节点返回-1 顶层节点返回0 树上的节点依次递增
     */
    public int getLayer() {
        if (layer == null) {
            if (!isValidNode()) {
                layer = -1;
            } else if (isTop()) {
                layer = 0;
            } else {
                layer = parent.getLayer() + 1;
            }
        }
        return layer;
    }

    /**
     * 获取路径 需要树化后才能调用
     * @param nameFunction 获取当前节点路径名的函数
     * @param separator 分隔符
     * @return 路径
     */
    public String getPath(Function<N, String> nameFunction, String separator) {
        if (parent == null) {
            @SuppressWarnings("unchecked")
            N n = (N) this;
            return nameFunction.apply(n);
        }
        @SuppressWarnings("unchecked")
        N n = (N) this;
        return parent.getPath(nameFunction, separator) + separator + nameFunction.apply(n);
    }

    /**
     * 获取路径 需要树化后才能调用
     * @param nameFunction 获取当前节点路径名的函数
     * @param separator 分隔符
     * @return 路径
     */
    public String getPath(Function<N, String> nameFunction, char separator) {
        return getPath(nameFunction, Character.toString(separator));
    }

    /**
     * 获取节点值
     * @return 节点值
     */
    public V getValue() {
        return value;
    }

    /**
     * 获取父节点
     * @return 父节点 其中顶层节点返回null
     */
    public N getParent() {
        return parent;
    }

    /**
     * 获取子节点
     * @return 子节点 其中叶子节点返回null
     */
    public Collection<N> getChildren() {
        return children;
    }

    /**
     * 是否顶层节点
     * @return 是否顶层节点
     */
    public boolean isTop() {
        return parent == null;
    }

    /**
     * 是否叶子节点
     * @return 是否叶子节点
     */
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    /**
     * 节点是否相等 使用的是唯一标识比较
     * @param o 比较的节点
     * @return 是否相等
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        return this == o
                || (o != null
                && getClass() == o.getClass()
                && extractIdentifier().equals(((N) o).extractIdentifier()));
    }

    /**
     * 对象哈希值 同equals 使用也是唯一标识
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(extractIdentifier());
    }

    /**
     * 转换为字符串表示
     * @return 字符串
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("layer=" + layer)
                .add("isTop=" + isTop())
                .add("isLeaf=" + isLeaf())
                .toString();
    }

    /**
     * 子节点的初始化方法 默认使用LinkedList
     * @return 初始化方法
     */
    protected Set<N> initChildren() {
        return new LinkedHashSet<>();
    }

    /**
     * 是否为有效节点 如果有些节点需要过滤 可以重写该方法 使其不参与树化过程
     * @return 是否为有效节点
     */
    protected boolean isValidNode() {
        return true;
    }

    /**
     * 是否为顶节点
     * @return 是否为顶节点
     */
    protected abstract boolean isTopNode();

    /**
     * 抽取全树范围内唯一的标识
     * @return 唯一标识
     */
    protected abstract I extractIdentifier();

    /**
     * 抽取父节点的唯一标识
     * @return 父节点的唯一标识
     */
    protected abstract I extractParentIdentifier();
}
