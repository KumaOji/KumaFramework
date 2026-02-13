/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tree;

import com.kuma.boot.common.support.tree.AbstractTreeNode;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractSortedTreeNode<V, I, N extends AbstractSortedTreeNode<V, I, N>>
extends AbstractTreeNode<V, I, N>
implements Comparable<N> {
    public AbstractSortedTreeNode(V value) {
        super(value);
    }

    @Override
    protected Set<N> initChildren() {
        return new TreeSet();
    }
}

