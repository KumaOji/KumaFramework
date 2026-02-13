/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import com.kuma.boot.common.tree.INode;
import com.kuma.boot.common.tree.TreeNode;
import java.util.List;
import java.util.Objects;

public class ForestNode extends TreeNode {
    private static final long serialVersionUID = -5188222097134746118L;
    private Object content;

    public ForestNode(Long id, Long parentId, Object content) {
        this.id = id;
        this.parentId = parentId;
        this.content = content;
    }

    public ForestNode(Object content) {
        this.content = content;
    }

    public ForestNode(Long id, Long parentId, List<INode> children, Boolean hasChildren, Object content, Integer sort) {
        super(id, parentId, children, hasChildren, sort);
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ForestNode{content=" + String.valueOf(this.content) + "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ForestNode that = (ForestNode)o;
        return Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.content);
    }
}

