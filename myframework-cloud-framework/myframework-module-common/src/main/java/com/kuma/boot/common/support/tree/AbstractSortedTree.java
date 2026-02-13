/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tree;

import com.kuma.boot.common.support.tree.AbstractSortedTreeNode;
import com.kuma.boot.common.support.tree.AbstractTree;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractSortedTree<V, I, N extends AbstractSortedTreeNode<V, I, N>>
extends AbstractTree<V, I, N> {
    public <C extends Collection<N>> AbstractSortedTree(C nodes) {
        super(nodes);
    }

    public <C extends Collection<N>> AbstractSortedTree(C nodes, AbstractTree.OrphanPolicy orphanPolicy) {
        super(nodes, orphanPolicy);
    }

    public <C extends Collection<N>> AbstractSortedTree(C nodes, boolean noRoot) {
        super(nodes, noRoot);
    }

    public <C extends Collection<N>> AbstractSortedTree(C nodes, boolean noRoot, AbstractTree.OrphanPolicy orphanPolicy) {
        super(nodes, noRoot, orphanPolicy);
    }

    @Override
    protected Set<N> initTopNodes() {
        return new TreeSet();
    }
}

