/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tree;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

public abstract class AbstractTreeNode<V, I, N extends AbstractTreeNode<V, I, N>> {
    protected final V value;
    private Integer layer;
    protected N parent;
    protected Set<N> children;

    public AbstractTreeNode(V value) {
        this.value = value;
    }

    protected void addChild(N node) {
        if (this.children == null) {
            this.children = this.initChildren();
        }
        this.children.add(node);
        AbstractTreeNode parent = this;
        ((AbstractTreeNode)node).parent = parent;
    }

    protected void isolate() {
        if (this.parent != null && ((AbstractTreeNode)this.parent).children != null) {
            ((AbstractTreeNode)this.parent).children.remove(this);
            this.parent = null;
        }
        if (this.children != null) {
            this.children.forEach(child -> {
                child.parent = null;
            });
            this.children.clear();
        }
    }

    public int getLayer() {
        if (this.layer == null) {
            this.layer = !this.isValidNode() ? Integer.valueOf(-1) : (this.isTop() ? Integer.valueOf(0) : Integer.valueOf(((AbstractTreeNode)this.parent).getLayer() + 1));
        }
        return this.layer;
    }

    public String getPath(Function<N, String> nameFunction, String separator) {
        if (this.parent == null) {
            AbstractTreeNode n = this;
            return nameFunction.apply(n);
        }
        AbstractTreeNode n = this;
        return ((AbstractTreeNode)this.parent).getPath(nameFunction, separator) + separator + nameFunction.apply(n);
    }

    public String getPath(Function<N, String> nameFunction, char separator) {
        return this.getPath(nameFunction, Character.toString(separator));
    }

    public V getValue() {
        return this.value;
    }

    public N getParent() {
        return this.parent;
    }

    public Collection<N> getChildren() {
        return this.children;
    }

    public boolean isTop() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

    public boolean equals(Object o) {
        return this == o || o != null && this.getClass() == o.getClass() && this.extractIdentifier().equals(((AbstractTreeNode)o).extractIdentifier());
    }

    public int hashCode() {
        return Objects.hash(this.extractIdentifier());
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("value=" + String.valueOf(this.value)).add("layer=" + this.layer).add("isTop=" + this.isTop()).add("isLeaf=" + this.isLeaf()).toString();
    }

    protected Set<N> initChildren() {
        return new LinkedHashSet();
    }

    protected boolean isValidNode() {
        return true;
    }

    protected abstract boolean isTopNode();

    protected abstract I extractIdentifier();

    protected abstract I extractParentIdentifier();
}

