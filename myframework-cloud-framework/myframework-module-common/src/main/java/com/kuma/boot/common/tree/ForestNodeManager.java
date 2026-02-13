/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import com.kuma.boot.common.tree.INode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForestNodeManager<T extends INode> {
    private List<T> list;
    private List<Long> parentIds = new ArrayList<Long>();

    public ForestNodeManager() {
    }

    public ForestNodeManager(List<T> items) {
        this.list = items;
    }

    public ForestNodeManager(List<T> list, List<Long> parentIds) {
        this.list = list;
        this.parentIds = parentIds;
    }

    public INode getTreeNodeAT(Long id) {
        for (INode forestNode : this.list) {
            if (forestNode.getId().longValue() != id.longValue()) continue;
            return forestNode;
        }
        return null;
    }

    public void addParentId(Long parentId) {
        this.parentIds.add(parentId);
    }

    public List<T> getRoot() {
        ArrayList<INode> roots = new ArrayList<INode>();
        for (INode forestNode : this.list) {
            if (forestNode.getParentId() != 0L && !this.parentIds.contains(forestNode.getId())) continue;
            roots.add(forestNode);
        }
        return (List<T>) roots;
    }

    public String toString() {
        return "ForestNodeManager{list=" + String.valueOf(this.list) + ", parentIds=" + String.valueOf(this.parentIds) + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ForestNodeManager that = (ForestNodeManager)o;
        return Objects.equals(this.list, that.list) && Objects.equals(this.parentIds, that.parentIds);
    }

    public int hashCode() {
        return Objects.hash(this.list, this.parentIds);
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<Long> getParentIds() {
        return this.parentIds;
    }

    public void setParentIds(List<Long> parentIds) {
        this.parentIds = parentIds;
    }
}

