/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.tree;

import com.kuma.boot.common.tree.INode;
import com.kuma.boot.common.tree.TreeNode;
import java.util.List;
import java.util.Objects;

public class MapperNode
extends TreeNode {
    private String title;
    private Long key;
    private Long value;

    public MapperNode() {
    }

    public MapperNode(String title, Long key, Long value) {
        this.title = title;
        this.key = key;
        this.value = value;
    }

    public MapperNode(Long id, Long parentId, List<INode> children, Integer sort, Boolean hasChildren, String title, Long key, Long value) {
        super(id, parentId, children, hasChildren, sort);
        this.title = title;
        this.key = key;
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getKey() {
        return this.key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Long getValue() {
        return this.value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MapperNode{title='" + this.title + "', key=" + this.key + ", value=" + this.value + "} " + super.toString();
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
        MapperNode that = (MapperNode)o;
        return Objects.equals(this.title, that.title) && Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.title, this.key, this.value);
    }
}

