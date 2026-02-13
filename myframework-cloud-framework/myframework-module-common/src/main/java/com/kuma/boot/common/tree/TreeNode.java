/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import com.kuma.boot.common.tree.INode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeNode implements INode {
    protected Long id;
    protected Long parentId;
    protected List<? extends INode> children = new ArrayList<INode>();
    private Boolean hasChildren = false;
    private Integer sort = 0;

    public TreeNode() {
    }

    public TreeNode(Long id, Long parentId, List<INode> children, Boolean hasChildren, Integer sort) {
        this.id = id;
        this.parentId = parentId;
        this.children = children;
        this.hasChildren = hasChildren;
        this.sort = sort;
    }

    @Override
    public Boolean getHasChildren() {
        if (this.children.size() > 0) {
            return true;
        }
        return this.hasChildren;
    }

    public String toString() {
        return "TreeNode{id=" + this.id + ", parentId=" + this.parentId + ", children=" + String.valueOf(this.children) + ", hasChildren=" + this.hasChildren + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TreeNode treeNode = (TreeNode)o;
        return Objects.equals(this.id, treeNode.id) && Objects.equals(this.parentId, treeNode.parentId) && Objects.equals(this.children, treeNode.children) && Objects.equals(this.hasChildren, treeNode.hasChildren);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.parentId, this.children, this.hasChildren);
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public <T extends INode> List<T> getChildren() {
        return null;
    }

    public void setChildren(List<? extends INode> children) {
        this.children = children;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Integer getSort() {
        return this.sort;
    }

    public void setSort(Integer sort) {
        this.sort = Objects.isNull(sort) ? 0 : sort;
    }
}

